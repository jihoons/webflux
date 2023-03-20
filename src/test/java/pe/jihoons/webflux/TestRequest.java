package pe.jihoons.webflux;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pe.jihoons.webflux.service.AsyncService;
import pe.jihoons.webflux.service.ParallelStreamService;
import pe.jihoons.webflux.service.ReactorService;
import pe.jihoons.webflux.service.UserService;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class TestRequest {
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private UserService userService;
    @Autowired
    private ParallelStreamService parallelStreamService;
    @Autowired
    private ReactorService reactorService;

    private final int queueSize = 1000;

    @Test
    void testUserService() {
        userService.requestService(queueSize);

        try {
            TimeUnit.SECONDS.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testParallelStream() {
        parallelStreamService.requestService(queueSize);

        try {
            TimeUnit.SECONDS.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testReactiveStream() {
        reactorService.doRequestService(queueSize);

        try {
            TimeUnit.SECONDS.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
