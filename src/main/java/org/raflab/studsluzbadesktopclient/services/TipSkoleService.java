package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.SpringDataPagedResponse;
import org.raflab.studsluzbacommon.dto.request.TipSkoleRequestDTO;
import org.raflab.studsluzbacommon.dto.response.TipSkoleResponseDTO;
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
public class TipSkoleService {
    private WebClient webClient;

    private String createURL() {
        return createURL("");
    }

    private String createURL(String path) {
        return "tip-skole/" + path;
    }

    public Flux<TipSkoleResponseDTO> fetchTipSkole() {
        return webClient.get()
                .uri(createURL())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SpringDataPagedResponse<TipSkoleResponseDTO>>() {})
                .flatMapMany(response -> {
                    if (response.getContent() != null) {
                        return Flux.fromIterable(response.getContent());
                    }
                    return Flux.empty();
                });
    }

    public Mono<TipSkoleResponseDTO> createTipSkole(String tip){
        TipSkoleRequestDTO body = new TipSkoleRequestDTO();
        body.setTip(tip);

        return webClient.post()
                .uri(createURL())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(TipSkoleResponseDTO.class);
    }

    public Mono<Boolean> deleteTipSkole(TipSkoleResponseDTO tipSkoleResponseDTO) {
        return webClient.delete()
                .uri(createURL(tipSkoleResponseDTO.getId().toString()))
                .retrieve()
                .onStatus(status -> status.value() == 409, clientResponse ->
                        Mono.error(new ConflictException("Tip Skole is in use and cannot be deleted.")))
                .onStatus(status -> status.value() == 404, clientResponse ->
                        Mono.error(new ResourceNotFoundException("Tip Skole is already deleted.")))
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new CommunicationException(clientResponse.statusCode().toString())))
                .toBodilessEntity()
                .thenReturn(true);
    }
}
