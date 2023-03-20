package pe.jihoons.webflux.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final AsyncService asyncService;
    public void doService(int limit) {
        log.info("service start");
        Stream.iterate(1, rc -> rc + 1).limit(limit).forEach(value -> {
            asyncService.saveRequest(String.valueOf(value));
        });
        log.info("service end");
    }

    private final AtomicLong counter = new AtomicLong(0L);
    public void requestService(int limit) {
        log.info("service start");
        counter.set(limit);
        Stream.iterate(1, rc -> rc + 1).limit(limit).forEach(value -> {
            asyncService.getRequest(counter, String.valueOf(value));
        });
//        log.info("service end");
    }
}
