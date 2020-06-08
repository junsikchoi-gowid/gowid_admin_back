package com.nomadconnection.dapp.api.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@RequiredArgsConstructor
@Configuration
public class WebClientConfig {

    @Value("${gateway.aws.domain}")
    private String GATEWAY_AWS_DOMAIN;

    @Bean
    public WebClient gwClient() {
        return WebClient.builder()
                .baseUrl(GATEWAY_AWS_DOMAIN)
                .build();
    }

    private String getUrl(String protocol, String host) {
        return new StringBuilder().append(protocol).append("://").append(host).toString();
    }
}
