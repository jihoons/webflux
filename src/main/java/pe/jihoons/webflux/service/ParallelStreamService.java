package pe.jihoons.webflux.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParallelStreamService {
    private final WebClient webClient;

    public void doService(int max)  {
        log.info("service start");
        new ForkJoinPool(Math.min(max, Runtime.getRuntime().availableProcessors() * 2)).submit(() -> {
            Stream.iterate(1, rc -> rc + 1).limit(max).parallel().forEach(value -> saveRequest(String.valueOf(value)));
        });
        log.info("service end");
    }

    public void saveRequest(String message) {
        log.info("parallel method start");
        try {
            log.info("task {}", message);
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("parallel method end");
    }

    private final AtomicLong counter = new AtomicLong(0L);
    private final AtomicLong errorCounter = new AtomicLong(0L);
    public void requestService(int max)  {
        log.info("service start");
        counter.set(max);
        new ForkJoinPool(Math.min(max, Runtime.getRuntime().availableProcessors() * 2)).submit(() -> {
            Stream.iterate(1, rc -> rc + 1).limit(max).parallel().forEach(value -> getRequest(String.valueOf(value)));
        });
//        log.info("service end");
    }

    public void getRequest(String message) {
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
    }
}
