package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.request.VisokoskolskaUstanovaRequestDTO;
import org.raflab.studsluzbacommon.dto.response.VisokoskolskaUstanovaResponseDTO;
import org.raflab.studsluzbadesktopclient.exceptions.ConflictException;
import org.raflab.studsluzbadesktopclient.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.naming.CommunicationException;

@Service
@AllArgsConstructor
public class VisokoskolskaUstanovaService {
    private WebClient webClient;

    private String createURL(String path) {
        return "visokoskolska-ustanova/" + path;
    }

    public Flux<VisokoskolskaUstanovaResponseDTO> fetchVisokoskolskaUstanove(){
        return webClient.get()
                .uri(createURL(""))
                .retrieve()
                .bodyToFlux(VisokoskolskaUstanovaResponseDTO.class);
    }

    public Mono<Long> createVisokoskolskaUstanova(String naziv){
        VisokoskolskaUstanovaRequestDTO body = new VisokoskolskaUstanovaRequestDTO();
        body.setNaziv(naziv);

        return webClient.post()
                .uri(createURL(""))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<Boolean> deleteVisokoskolskaUstanova(VisokoskolskaUstanovaResponseDTO visokoskolskaUstanovaResponseDTO) {
        return webClient.delete()
                .uri(createURL(visokoskolskaUstanovaResponseDTO.getId().toString()))
                .retrieve()
                .onStatus(status -> status.value() == 409, clientResponse ->
                        Mono.error(new ConflictException("Visokoskolska Ustanova is in use and cannot be deleted.")))
                .onStatus(status -> status.value() == 404, clientResponse ->
                        Mono.error(new ResourceNotFoundException("Visokoskolska Ustanova is already deleted.")))
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new CommunicationException(clientResponse.statusCode().toString())))
                .bodyToMono(Boolean.class);
    }
}
