package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.SpringDataPagedResponse;
import org.raflab.studsluzbacommon.dto.response.NacinFinansiranja;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class NacinFinansiranjaService {
    private WebClient webClient;

    public Flux<NacinFinansiranja> fetchNaciniFinansiranja() {
        return webClient.get()
                .uri("/nacin-finansiranja")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SpringDataPagedResponse<NacinFinansiranja>>() {})
                .flatMapMany(response -> Flux.fromIterable(response.getContent()));
    }

    public Mono<NacinFinansiranja> createNacinFinansiranja(String nacinFinansiranja){
        NacinFinansiranja body = new NacinFinansiranja();
        body.setNacin(nacinFinansiranja);

        return webClient.post()
                .uri("/nacin-finansiranja")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(NacinFinansiranja.class);
    }

    public Mono<Boolean> deleteNacinFinansiranja(NacinFinansiranja nacinFinansiranja){
        return webClient.delete()
                .uri("/nacin-finansiranja/{id}", nacinFinansiranja.getId())
                .retrieve()
                .toBodilessEntity()
                .thenReturn(true);
    }
}
