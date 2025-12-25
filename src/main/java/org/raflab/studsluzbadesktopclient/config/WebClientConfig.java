package org.raflab.studsluzbadesktopclient.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.exceptions.ServerCommunicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;


@Configuration(proxyBeanMethods = false)
public class WebClientConfig {
    @Autowired
    private ConfigProperties configProperties;

    public WebClientConfig(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .compress(false);
        return WebClient.builder()
            .baseUrl(configProperties.getBaseUrl())
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultStatusHandler(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new ServerCommunicationException(clientResponse.statusCode().toString())))
            .defaultStatusHandler(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(new InvalidDataException(this.extractErrorMessage(errorBody))))
            )
            .build();
    }

    private String extractErrorMessage(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);
            return node.get("errors").asText();
        } catch (Exception e) {
            return json;
        }
    }
}