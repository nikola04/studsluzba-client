package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.response.SkolskaGodinaResponseDTO;
import org.raflab.studsluzbadesktopclient.exceptions.ServerCommunicationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class SkolskaGodinaService {
    private WebClient webClient;
    private String createURL() {
        return "skolskagodina/";
    }
    public Flux<SkolskaGodinaResponseDTO> fetchSkolskeGodine(){

        return webClient.get()
                .uri(createURL())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new ServerCommunicationException(clientResponse.statusCode().toString())))
                .bodyToFlux(SkolskaGodinaResponseDTO.class);
    }
}
