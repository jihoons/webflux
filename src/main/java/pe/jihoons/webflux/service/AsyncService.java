package pe.jihoons.webflux.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncService {
    private final WebClient webClient;
    private final Scheduler blockingScheduler = Schedulers.newBoundedElastic(Runtime.getRuntime().availableProcessors() * 2, 10000, "blockScheduler");
    private final Scheduler computeScheduler = Schedulers.newBoundedElastic(Runtime.getRuntime().availableProcessors() * 2, 10000, "computeScheduler");

    public void doService(int max) {
        log.info("service start");
        Stream.iterate(1, rc -> rc + 1).limit(max).forEach(value -> {
            this.saveRequest(String.valueOf(value));
        });
        log.info("service end");
    }

    @Async
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
        counter.set(max);
        Stream.iterate(1, rc -> rc + 1).limit(max).forEach(value -> {
            this.getRequest(counter, String.valueOf(value));
        });
        log.info("service end");
    }

    private final AtomicLong counter = new AtomicLong(0L);
    private final AtomicLong errorCounter = new AtomicLong(0L);

    @Async
    public void getRequest(AtomicLong counter, String message) {
//        log.info("async method start");
        try {
            String response = webClient.get().uri(URI.create("http://localhost:8081/echo/" + message))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

//            log.info("response {}", response);
        } catch (Exception e) {
            errorCounter.incrementAndGet();
//            e.printStackTrace();
        }
        long count = counter.decrementAndGet();
        if (count == 0) {
            log.info("async method end");
        }
//        if (count % 10 == 0) {
//            log.info("response count {}", count);
//        }
    }

    public <T> Mono<T> async(Callable<T> callable) {
        return Mono.fromCallable(callable).subscribeOn(blockingScheduler).publishOn(computeScheduler);
    }
}
