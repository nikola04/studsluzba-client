package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.request.IspitRequest;
import org.raflab.studsluzbacommon.dto.response.IspitIzlazakResponse;
import org.raflab.studsluzbacommon.dto.response.IspitPrijavaResponse;
import org.raflab.studsluzbacommon.dto.response.IspitRezultatResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class IspitService {
    private final RestClient restClient;
    private WebClient webClient;

    public Mono<Long> saveIspit(IspitRequest request) {
        return webClient.post()
                .uri("ispit/")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<Boolean> delete(Long id) {
        return webClient.delete()
                .uri("ispit/{id}", id)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Flux<IspitPrijavaResponse> fetchIspitPrijava(Long ispitId) {
        return webClient.get()
                .uri("ispit/{id}/prijava", ispitId)
                .retrieve()
                .bodyToFlux(IspitPrijavaResponse.class);
    }

    public Flux<IspitRezultatResponse> fetchIspitRezultat(Long ispitId) {
        return webClient.get()
                .uri("ispit/{id}/rezultati", ispitId)
                .retrieve()
                .bodyToFlux(IspitRezultatResponse.class);
    }

    public List<IspitRezultatResponse> fetchIspitRezultatSync(Long ispitId) {
        return restClient.get()
                .uri("ispit/{id}/rezultati", ispitId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public Flux<IspitIzlazakResponse> fetchIspitIzlazak(Long ispitId) {
        return webClient.get()
                .uri("ispit/{id}/izlazak", ispitId)
                .retrieve()
                .bodyToFlux(IspitIzlazakResponse.class);
    }
}
