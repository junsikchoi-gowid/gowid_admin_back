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
    public WebClient kcbClient() {
        return WebClient.builder()
                .baseUrl(this.getUrl("http", "gowid-gw.com/kcb/sms"))
                .build();
    }

    private String getUrl(String protocol, String host) {
        return new StringBuilder().append(protocol).append("://").append(host).toString();
    }
}
