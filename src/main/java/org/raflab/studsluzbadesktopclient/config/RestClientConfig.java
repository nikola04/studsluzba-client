package org.raflab.studsluzbadesktopclient.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Autowired
    private ConfigProperties configProperties;

    public RestClientConfig(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
            .baseUrl(configProperties.getBaseUrl())
            .defaultHeaders(headers -> headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .build();
    }
}