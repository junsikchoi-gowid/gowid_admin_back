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

    @Value("${gateway.idc.shinhan}")
    private String GATEWAY_IDC_SHINHAN;

    @Value("${gateway.idc.lotte}")
    private String GATEWAY_IDC_LOTTE;

    @Bean
    public WebClient gwShinhanClient() {
        return WebClient.builder()
                .baseUrl(GATEWAY_IDC_SHINHAN)
                .build();
    }

    @Bean
    public WebClient gwLotteClient() {
        return WebClient.builder()
                .baseUrl(GATEWAY_IDC_LOTTE)
                .build();
    }

    private String getUrl(String protocol, String host) {
        return new StringBuilder().append(protocol).append("://").append(host).toString();
    }
}
