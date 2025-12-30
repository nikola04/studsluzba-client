package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class PredmetService {
    private WebClient webClient;

    public Mono<Boolean> deletePredmet(Long predmetId) {
        return webClient.delete()
                .uri("/predmet/{id}", predmetId)
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}
