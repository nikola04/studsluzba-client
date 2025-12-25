package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.request.SkolskaGodinaRequestDTO;
import org.raflab.studsluzbacommon.dto.response.SkolskaGodinaResponseDTO;
import org.raflab.studsluzbadesktopclient.exceptions.ConflictException;
import org.raflab.studsluzbadesktopclient.exceptions.ResourceNotFoundException;
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
    private String createURL(String path) {
        return "skolskagodina/" + path;
    }

    public Flux<SkolskaGodinaResponseDTO> fetchSkolskeGodine(){
        return webClient.get()
                .uri(createURL(""))
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new ServerCommunicationException(clientResponse.statusCode().toString())))
                .bodyToFlux(SkolskaGodinaResponseDTO.class);
    }

    public Mono<Long> createSkolskaGodina(Integer godina, boolean aktivan){
        SkolskaGodinaRequestDTO body = new SkolskaGodinaRequestDTO();
        body.setGodina(godina);
        body.setAktivan(aktivan);

        return webClient.post()
                .uri(createURL(""))
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.value() == 409, clientResponse ->
                        Mono.error(new ConflictException("Skolska Godina already exists.")))
                .bodyToMono(Long.class);
    }

    public Mono<SkolskaGodinaResponseDTO> updateSkolskaGodina(Long id, Integer godina, boolean aktivan){
        SkolskaGodinaRequestDTO body = new SkolskaGodinaRequestDTO();
        body.setGodina(godina);
        body.setAktivan(aktivan);

        return webClient.patch()
                .uri(createURL(id.toString()))
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.value() == 409, clientResponse ->
                        Mono.error(new ConflictException("'godina' in Skolska Godina is already in use.")))
                .onStatus(status -> status.value() == 404, clientResponse ->
                        Mono.error(new ResourceNotFoundException("Skolska Godina doesnt exist.")))
                .bodyToMono(SkolskaGodinaResponseDTO.class);
    }
    public Mono<Boolean> deleteSkolskaGodina(Long id){
        return webClient.delete()
                .uri(createURL(id.toString()))
                .retrieve()
                .onStatus(status -> status.value() == 409, clientResponse ->
                        Mono.error(new ConflictException("Skolska Godina is in use and cannot be deleted.")))
                .onStatus(status -> status.value() == 404, clientResponse ->
                        Mono.error(new ResourceNotFoundException("Skolska Godina doesnt exist.")))
                .bodyToMono(Boolean.class);
    }
}
