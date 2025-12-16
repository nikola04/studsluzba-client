package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.SpringDataPagedResponse;
import org.raflab.studsluzbacommon.dto.request.PredispitneVrstaRequestDTO;
import org.raflab.studsluzbacommon.dto.response.PredispitneVrstaResponseDTO;
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
public class PredispitneVrstaService {
    private WebClient webClient;

    private String createURL(String path) {
        return "predispitna-obaveza-vrsta/" + path;
    }

    public Flux<PredispitneVrstaResponseDTO> fetchPredispitneVrsta(){
        return webClient.get()
                .uri(createURL(""))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SpringDataPagedResponse<PredispitneVrstaResponseDTO>>() {})
                .flatMapMany(response -> {
                    if (response.getContent() != null) {
                        return Flux.fromIterable(response.getContent());
                    }
                    return Flux.empty();
                });
    }

    public Mono<PredispitneVrstaResponseDTO> createPredispitneVrsta(String vrsta){
        PredispitneVrstaRequestDTO body = new PredispitneVrstaRequestDTO();
        body.setVrsta(vrsta);

        return webClient.post()
                .uri(createURL(""))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(PredispitneVrstaResponseDTO.class);
    }

    public Mono<Boolean> deletePredispitneVrsta(PredispitneVrstaResponseDTO predispitneVrstaResponseDTO) {
        return webClient.delete()
                .uri(createURL(predispitneVrstaResponseDTO.getId().toString()))
                .retrieve()
                .onStatus(status -> status.value() == 409, clientResponse ->
                        Mono.error(new ConflictException("Predispitne Vrsta is in use and cannot be deleted.")))
                .onStatus(status -> status.value() == 404, clientResponse ->
                        Mono.error(new ResourceNotFoundException("Predispitne Vrsta is already deleted.")))
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new CommunicationException(clientResponse.statusCode().toString())))
                .toBodilessEntity()
                .thenReturn(true);
    }
}
