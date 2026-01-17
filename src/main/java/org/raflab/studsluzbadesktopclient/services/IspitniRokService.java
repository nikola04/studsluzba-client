package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.request.IspitniRokRequest;
import org.raflab.studsluzbacommon.dto.response.IspitResponse;
import org.raflab.studsluzbacommon.dto.response.IspitniRokResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class IspitniRokService {
    private WebClient webClient;

    public Flux<IspitniRokResponse> fetchIspitniRok(){
        return webClient
                .get()
                .uri("ispitni-rok/")
                .retrieve()
                .bodyToFlux(IspitniRokResponse.class);
    }

    public Mono<Long> saveIspitniRok(IspitniRokRequest request) {
        return webClient.post()
                .uri("ispitni-rok/")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<Boolean> delete(Long id) {
        return webClient.delete()
                .uri("ispitni-rok/{id}", id)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Flux<IspitResponse> fetchIspitByRokId(Long id) {
        return webClient.get()
                .uri("ispitni-rok/{id}/ispit", id)
                .retrieve()
                .bodyToFlux(IspitResponse.class);
    }
}
