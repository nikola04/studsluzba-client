package org.raflab.studsluzbadesktopclient.services;


import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.request.NastavnikRequest;
import org.raflab.studsluzbacommon.dto.response.NastavnikResponseDTO;
import org.raflab.studsluzbadesktopclient.exceptions.ConflictException;
import org.raflab.studsluzbadesktopclient.exceptions.ResourceNotFoundException;
import org.raflab.studsluzbadesktopclient.exceptions.ServerCommunicationException;
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

    public Mono<NastavnikResponseDTO> updateNastavnik(Long id, NastavnikRequest request) {
        return webClient.patch()
                .uri("/nastavnik/{id}", id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NastavnikResponseDTO.class);
    }

    public Mono<Void> deleteNastavnik(Long id) {
        return webClient
                .delete()
                .uri("/nastavnik/{id}", id)
                .exchangeToMono(response -> {

                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.empty();
                    }

                    if (response.statusCode().value() == 409) {
                        return Mono.error(
                                new ConflictException(
                                        "Ne možete obrisati nastavnika dok je vezan za druge podatke."
                                )
                        );
                    }

                    if (response.statusCode().value() == 404) {
                        return Mono.error(
                                new ResourceNotFoundException("Nastavnik ne postoji.")
                        );
                    }

                    return Mono.error(
                            new ServerCommunicationException("Greška na serveru.")
                    );
                });
    }
}