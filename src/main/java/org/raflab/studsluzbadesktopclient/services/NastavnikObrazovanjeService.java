package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.SpringDataPagedResponse;
import org.raflab.studsluzbacommon.dto.response.NastavnikObrazovanjeResponseDTO;
import org.raflab.studsluzbacommon.dto.response.ZvanjeResponseDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
@Service
@AllArgsConstructor
public class NastavnikObrazovanjeService {
    private WebClient webClient;

    private String createURL(String path) {
        return "obrazovanje/" + path;
    }
    public Flux<Object> fetchObrazovanja() {
        return webClient.get()
                .uri(createURL(""))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SpringDataPagedResponse<NastavnikObrazovanjeResponseDTO>>() {})
                .flatMapMany(response -> {
                    if (response.getContent() != null) {
                        return Flux.fromIterable(response.getContent());
                    }
                    return Flux.empty();
                });
    }
}
