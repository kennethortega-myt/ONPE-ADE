package pe.gob.onpe.sceorcbackend.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
public class AsyncRestTemplateConfig {

    @Value("${sasa.timeout-miliseconds}")
    private Long timeoutMiliseconds;

    @Bean
    @Primary
    public WebClient webClient() {
        return WebClient.create();
    }

    @Bean
    WebClient webClientSasa() {
        Integer timeoutSeconds = (timeoutMiliseconds.intValue() / 1000);
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutMiliseconds.intValue())
                .responseTimeout(Duration.ofMillis(timeoutMiliseconds))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(timeoutSeconds))
                        .addHandlerLast(new WriteTimeoutHandler(timeoutSeconds)));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
