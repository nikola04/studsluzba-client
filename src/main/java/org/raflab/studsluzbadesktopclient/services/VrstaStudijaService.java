package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.request.VrstaStudijaRequestDTO;
import org.raflab.studsluzbacommon.dto.response.VrstaStudijaResponseDTO;
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
public class VrstaStudijaService {
    private WebClient webClient;

    private String createURL(String path) {
        return "vrsta-studija/" + path;
    }

    public Flux<VrstaStudijaResponseDTO> fetchVrstaStudija(){
        return webClient.get()
                .uri(createURL(""))
                .retrieve()
                .bodyToFlux(VrstaStudijaResponseDTO.class);
    }

    public Mono<Long> createVrstaStudija(String naziv, String oznaka){
        VrstaStudijaRequestDTO body = new VrstaStudijaRequestDTO();
        body.setNaziv(naziv);
        body.setOznaka(oznaka);

        return webClient.post()
                .uri(createURL(""))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<Boolean> deleteVrstaStudija(VrstaStudijaResponseDTO vrstaStudijaResponseDTO) {
        return webClient.delete()
                .uri(createURL(vrstaStudijaResponseDTO.getId().toString()))
                .retrieve()
                .onStatus(status -> status.value() == 409, clientResponse ->
                        Mono.error(new ConflictException("Vrsta Studija is in use and cannot be deleted.")))
                .onStatus(status -> status.value() == 404, clientResponse ->
                        Mono.error(new ResourceNotFoundException("Vrsta Studija is already deleted.")))
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new CommunicationException(clientResponse.statusCode().toString())))
                .bodyToMono(Boolean.class);
    }
}
