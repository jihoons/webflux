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
class WebfluxApplicationTests {
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private UserService userService;
    @Autowired
    private ParallelStreamService parallelStreamService;
    @Autowired
    private ReactorService reactorService;

    private final int queueSize = 100;

    @Test
    void testAsyncService() {
        asyncService.doService(queueSize);
    }

    @Test
    void testUserService() {
        userService.doService(queueSize);

        try {
            TimeUnit.SECONDS.sleep(5L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testParallelStream() {
        parallelStreamService.doService(queueSize);

        try {
            TimeUnit.SECONDS.sleep(5L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testReactiveStream() {
        reactorService.doService(queueSize);

        try {
            TimeUnit.SECONDS.sleep(5L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAsyncReactiveStream() {
        reactorService.doAsyncService(queueSize);

        try {
            TimeUnit.SECONDS.sleep(5L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAsyncServiceReactive() {
        reactorService.useAsyncService(queueSize);
        try {
            TimeUnit.SECONDS.sleep(5L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
