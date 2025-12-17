package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.response.NastavnikResponseDTO;
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
}