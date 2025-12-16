package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.SpringDataPagedResponse;
import org.raflab.studsluzbacommon.dto.request.UzaNaucnaOblastRequestDTO;
import org.raflab.studsluzbacommon.dto.response.UzaNaucnaOblastResponseDTO;
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
public class UzaNaucnaOblastService {
    private WebClient webClient;

    private String createURL(String path) {
        return "uza-naucna-oblast/" + path;
    }

    public Flux<UzaNaucnaOblastResponseDTO> fetchUzaNaucnaOblast(){
        return webClient.get()
                .uri(createURL(""))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SpringDataPagedResponse<UzaNaucnaOblastResponseDTO>>() {})
                .flatMapMany(response -> {
                    if (response.getContent() != null) {
                        return Flux.fromIterable(response.getContent());
                    }
                    return Flux.empty();
                });
    }

    public Mono<UzaNaucnaOblastResponseDTO> createUzaNaucnaOblast(String oblast){
        UzaNaucnaOblastRequestDTO body = new UzaNaucnaOblastRequestDTO();
        body.setUzaNaucnaOblast(oblast);

        return webClient.post()
                .uri(createURL(""))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(UzaNaucnaOblastResponseDTO.class);
    }

    public Mono<Boolean> deleteUzaNaucnaOblast(UzaNaucnaOblastResponseDTO uzaNaucnaOblastResponseDTO) {
        return webClient.delete()
                .uri(createURL(uzaNaucnaOblastResponseDTO.getId().toString()))
                .retrieve()
                .onStatus(status -> status.value() == 409, clientResponse ->
                        Mono.error(new ConflictException("Uza Naucna Oblast is in use and cannot be deleted.")))
                .onStatus(status -> status.value() == 404, clientResponse ->
                        Mono.error(new ResourceNotFoundException("Uza Naucna Oblast is already deleted.")))
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new CommunicationException(clientResponse.statusCode().toString())))
                .toBodilessEntity()
                .thenReturn(true);
    }
}
