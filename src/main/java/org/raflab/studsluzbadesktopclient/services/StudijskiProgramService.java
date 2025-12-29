package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.response.StudijskiProgramResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class StudijskiProgramService {
    private WebClient webClient;

    public Flux<StudijskiProgramResponseDTO> fetchStudijskiProgram() {
        return webClient.get()
                .uri("/studijski-program/")
                .retrieve()
                .bodyToFlux(StudijskiProgramResponseDTO.class);
    }
}
