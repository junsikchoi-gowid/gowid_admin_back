package com.nomadconnection.dapp.api.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@RequiredArgsConstructor
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient gwClient() {
        return WebClient.builder()
                .baseUrl(this.getUrl("http", "10.10.40.171"))
                .build();
    }

    private String getUrl(String protocol, String host) {
        return new StringBuilder().append(protocol).append("://").append(host).toString();
    }
}
