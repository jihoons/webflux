package pe.jihoons.webflux.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReactorService {
    private final AsyncService asyncService;
    private final WebClient webClient;
    public void doService(int max) {
        log.info("service start");
        Flux.fromStream(Stream.iterate(1, rc -> rc + 1).limit(max)).doOnNext(value -> {
            this.saveRequest(String.valueOf(value));
        }).subscribe();
        log.info("service end");
    }

    public void doAsyncService(int max) {
        log.info("service start");
        int parallel = Runtime.getRuntime().availableProcessors() * 2;
//        Scheduler scheduler = Schedulers.newParallel("reactor", parallel);
        Scheduler scheduler = Schedulers.newBoundedElastic(parallel,1000, "reactor");
        Flux.fromStream(Stream.iterate(1, rc -> rc + 1).limit(max)).parallel().runOn(scheduler).doOnNext(value -> {
            this.saveRequest(String.valueOf(value));
        }).subscribe();
        log.info("service end");
    }

    public void useAsyncService(int max) {
        log.info("useAsyncService start");
        Flux.fromStream(Stream.iterate(1, rc -> rc + 1).limit(max))
                .flatMap(value -> asyncService.async(() -> {
                    saveRequest(String.valueOf(value));
                    return "";
                })).subscribe();
        log.info("useAsyncService end");
    }

    public void saveRequest(String message) {
        log.info("async method start");
        try {
            log.info("task {}", message);
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("async method end");
    }

    public void doRequestService(int max) {
        log.info("service start");
//        Flux.fromStream(Stream.iterate(1, rc -> rc + 1).limit(max)).flatMap(value -> asyncService.async(() -> {
//            this.getRequest(String.valueOf(value));
//            return "";
//        })).subscribe();
        counter.set(max);
        Flux.fromStream(Stream.iterate(1, rc -> rc + 1).limit(max)).doOnNext(value -> {
            this.getRequest(String.valueOf(value));
//            return "";
        }).subscribe();
//        log.info("end start");
    }

    private final AtomicLong counter = new AtomicLong(0L);
    private final AtomicLong errorCounter = new AtomicLong(0L);
    public void getRequest(String message) {
//        log.info("async method start");
        webClient.get().uri(URI.create("http://localhost:8081/echo/" + message))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(throwable -> {
                    long count = counter.decrementAndGet();
                    errorCounter.incrementAndGet();
                    if (count == 0L) {
                        log.info("async method end {}", errorCounter.get());
                    }
                })
                .doOnNext(response -> {
                    long count = counter.decrementAndGet();
                    if (count == 0L) {
                        log.info("async method end {}", errorCounter.get());
                    }
                })
                .subscribe();
    }
}
