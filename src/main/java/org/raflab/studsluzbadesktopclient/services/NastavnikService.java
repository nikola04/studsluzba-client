package org.raflab.studsluzbadesktopclient.services;

import javafx.beans.Observable;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.raflab.studsluzbacommon.dto.request.NastavnikRequestDTO;
import org.raflab.studsluzbacommon.dto.response.NastavnikResponseDTO;
import org.raflab.studsluzbadesktopclient.exceptions.ConflictException;
import org.raflab.studsluzbadesktopclient.exceptions.ResourceNotFoundException;
import org.raflab.studsluzbadesktopclient.exceptions.ServerCommunicationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class NastavnikService {

    private WebClient webClient;

    private String createURL(String pathEnd) {
        return "nastavnik/" + pathEnd;
    }

    private String createSearchUrl(String name, String lastName){
        if (name == null || lastName == null || (name.isEmpty() && lastName.isEmpty()))
            return createURL("");

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(createURL("search"));
        builder.queryParam("ime", name);
        builder.queryParam("prezime", lastName);
        return builder.toUriString();
    }

    public Mono<List<NastavnikResponseDTO>> searchNastavnik(String name, String lastName) {
        String url = createSearchUrl(name, lastName);

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse ->
                                Mono.error(new ServerCommunicationException(clientResponse.statusCode().toString())))
                .bodyToFlux(NastavnikResponseDTO.class)
                .collectList();
    }

    public Mono<NastavnikResponseDTO> updateNastavnik(Long id, NastavnikRequestDTO request) {
        return webClient.patch()
                .uri("nastavnik/" + id)
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