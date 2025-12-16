package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.SpringDataPagedResponse;
import org.raflab.studsluzbacommon.dto.request.NaucnaOblastRequestDTO;
import org.raflab.studsluzbacommon.dto.response.NaucnaOblastResponseDTO;
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
public class NaucnaOblastService {
    private WebClient webClient;

    private String createURL(String path) {
        return "naucna-oblast/" + path;
    }

    public Flux<NaucnaOblastResponseDTO> fetchNaucnaOblast(){
        return webClient.get()
                .uri(createURL(""))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SpringDataPagedResponse<NaucnaOblastResponseDTO>>() {})
                .flatMapMany(response -> {
                    if (response.getContent() != null) {
                        return Flux.fromIterable(response.getContent());
                    }
                    return Flux.empty();
                });
    }

    public Mono<NaucnaOblastResponseDTO> createNaucnaOblast(String oblast){
        NaucnaOblastRequestDTO body = new NaucnaOblastRequestDTO();
        body.setNaucnaOblast(oblast);

        return webClient.post()
                .uri(createURL(""))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(NaucnaOblastResponseDTO.class);
    }

    public Mono<Boolean> deleteNaucnaOblast(NaucnaOblastResponseDTO naucnaOblastResponseDTO) {
        return webClient.delete()
                .uri(createURL(naucnaOblastResponseDTO.getId().toString()))
                .retrieve()
                .onStatus(status -> status.value() == 409, clientResponse ->
                        Mono.error(new ConflictException("Naucna Oblast is in use and cannot be deleted.")))
                .onStatus(status -> status.value() == 404, clientResponse ->
                        Mono.error(new ResourceNotFoundException("Naucna Oblast is already deleted.")))
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new CommunicationException(clientResponse.statusCode().toString())))
                .toBodilessEntity()
                .thenReturn(true);
    }
}
