package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.SpringDataPagedResponse;
import org.raflab.studsluzbacommon.dto.request.ZvanjeRequestDTO;
import org.raflab.studsluzbacommon.dto.response.ZvanjeResponseDTO;
import org.raflab.studsluzbadesktopclient.exceptions.ConflictException;
import org.raflab.studsluzbadesktopclient.exceptions.ResourceNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.naming.CommunicationException;

@Service
@AllArgsConstructor
public class ZvanjeService {
    private WebClient webClient;

    private String createURL(String path) {
        return "zvanje/" + path;
    }

    public Flux<ZvanjeResponseDTO> fetchZvanje(){
        return webClient.get()
                .uri(createURL(""))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SpringDataPagedResponse<ZvanjeResponseDTO>>() {})
                .flatMapMany(response -> {
                    if (response.getContent() != null) {
                        return Flux.fromIterable(response.getContent());
                    }
                    return Flux.empty();
                });
    }

    public Mono<ZvanjeResponseDTO> createZvanje(String zvanje){
        ZvanjeRequestDTO body = new ZvanjeRequestDTO();
        body.setZvanje(zvanje);

        return webClient.post()
                .uri(createURL(""))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(ZvanjeResponseDTO.class);
    }

    public Mono<Boolean> deleteZvanje(ZvanjeResponseDTO zvanjeResponseDTO) {
        return webClient.delete()
                .uri(createURL(zvanjeResponseDTO.getId().toString()))
                .retrieve()
                .onStatus(status -> status.value() == 409, clientResponse ->
                        Mono.error(new ConflictException("Zvanje is in use and cannot be deleted.")))
                .onStatus(status -> status.value() == 404, clientResponse ->
                        Mono.error(new ResourceNotFoundException("Zvanje is already deleted.")))
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new CommunicationException(clientResponse.statusCode().toString())))
                .toBodilessEntity()
                .thenReturn(true);
    }
}
