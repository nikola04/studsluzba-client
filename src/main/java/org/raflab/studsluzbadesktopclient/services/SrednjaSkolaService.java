package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.response.SrednjaSkolaResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class SrednjaSkolaService {
    private WebClient webClient;

    private String createURL() {
        return "srednja-skola/";
    }

    public Flux<SrednjaSkolaResponseDTO> fetchSrednjaSkola(){
        return webClient.get()
                .uri(createURL())
                .retrieve()
                .bodyToFlux(SrednjaSkolaResponseDTO.class);
    }
}
