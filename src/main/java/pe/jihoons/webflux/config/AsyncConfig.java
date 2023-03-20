package pe.jihoons.webflux.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;
import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setQueueCapacity(50);
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }

    @Bean
    public WebClient webClient() {
        TcpClient tcpClient = TcpClient.newConnection();
        HttpClient httpClient = HttpClient.from(tcpClient);
        httpClient.keepAlive(true);
        ReactorClientHttpConnector httpConnector = new ReactorClientHttpConnector(httpClient);

//        HttpClient httpClient = HttpClient.create(provider);
        return WebClient.builder()
                .clientConnector(httpConnector)
                .build();
    }
}
