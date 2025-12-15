package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.PagedResponse;
import org.raflab.studsluzbacommon.dto.response.SkolskaGodinaResponseDTO;
import org.raflab.studsluzbacommon.dto.response.StudentResponseDTO;
import org.raflab.studsluzbadesktopclient.exceptions.ServerCommunicationException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.prefs.AbstractPreferences;

@Service
@AllArgsConstructor
public class SkolskaGodinaService {
    private WebClient webClient;
    private String createURL(String pathEnd) {
        return "skolskagodina/" + pathEnd;
    }
    public CompletableFuture<List<SkolskaGodinaResponseDTO>> getAllSkolskeGodine(){

        return webClient.get()
                .uri(createURL(""))
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new ServerCommunicationException(clientResponse.statusCode().toString())))
                .bodyToMono(new ParameterizedTypeReference<List<SkolskaGodinaResponseDTO>>() {})
                .toFuture();
    }
}
