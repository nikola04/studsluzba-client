package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.request.DrziPredmetRequest;
import org.raflab.studsluzbacommon.dto.request.PredmetRequest;
import org.raflab.studsluzbacommon.dto.response.DrziPredmetResponse;
import org.raflab.studsluzbacommon.dto.response.PredmetResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class PredmetService {
    private WebClient webClient;

    public Mono<PredmetResponse> updatePredmet(Long predmetId, String naziv, Integer espb, Boolean obavezan, String opis, String sifra, Long studijskiProgramId,Integer fondCasovaVezbe, Integer fondCasovaPredavanje){
        PredmetRequest body = new PredmetRequest();
        body.setNaziv(naziv);
        body.setEspb(espb);
        body.setObavezan(obavezan);
        body.setOpis(opis);
        body.setSifra(sifra);
        body.setStudijskiProgramId(studijskiProgramId);
        body.setFondCasovaPredavanja(fondCasovaPredavanje);
        body.setFondCasovaVezbe(fondCasovaVezbe);

        return webClient.patch()
                .uri("predmet/{id}", predmetId)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(PredmetResponse.class);
    }

    public Mono<Boolean> deletePredmet(Long predmetId) {
        return webClient.delete()
                .uri("/predmet/{id}", predmetId)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Flux<DrziPredmetResponse> getPredmetDrzi(Long predmetId){
        return webClient.get()
                .uri("predmet/{id}/drzi", predmetId)
                .retrieve()
                .bodyToFlux(DrziPredmetResponse.class);
    }

    public Mono<Long> createDrziPredmet(Long predmetId, Long nastavnikId, Long godinaId){
        DrziPredmetRequest body = new DrziPredmetRequest();
        body.setSkolskaGodinaId(godinaId);

        return webClient.post()
                .uri("/nastavnik/{nastavnikId}/predmet/{predmetId}/", nastavnikId, predmetId, godinaId)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<Boolean> deleteDrziPredmet(Long predmetId, Long nastavnikId, Long godinaId){
        return webClient.delete()
                .uri("/nastavnik/{nastavnikId}/predmet/{predmetId}/godina/{godinaId}", nastavnikId, predmetId, godinaId)
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}
