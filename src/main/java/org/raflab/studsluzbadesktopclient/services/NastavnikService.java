package org.raflab.studsluzbadesktopclient.services;


import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.request.NastavnikRequest;
import org.raflab.studsluzbacommon.dto.response.NastavnikResponseDTO;
import org.raflab.studsluzbadesktopclient.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@AllArgsConstructor
public class NastavnikService {

    private WebClient webClient;

    public Flux<NastavnikResponseDTO> searchNastavnik(String name, String lastName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/nastavnik/search")
                        .queryParam("ime", name)
                        .queryParam("prezime", lastName)
                        .build())
                .retrieve()
                .bodyToFlux(NastavnikResponseDTO.class);
    }

    public Mono<Long> createNastavnik(NastavnikRequest request){
        return webClient.post()
                .uri("/nastavnik/")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<NastavnikResponseDTO> updateNastavnik(Long id, NastavnikRequest request) {
        return webClient.put()
                .uri("/nastavnik/{id}", id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NastavnikResponseDTO.class);
    }

    public Mono<Boolean> deleteNastavnik(Long id) {
        return webClient
                .delete()
                .uri("/nastavnik/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404, clientResponse -> Mono.error(new ResourceNotFoundException("Nastavnik cannot be found.")))
                .bodyToMono(Boolean.class);
    }
}