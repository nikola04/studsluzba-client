package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.request.StudijskiProgramRequest;
import org.raflab.studsluzbacommon.dto.response.PredmetResponse;
import org.raflab.studsluzbacommon.dto.response.StudijskiProgramResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class StudijskiProgramService {
    private WebClient webClient;

    public Flux<StudijskiProgramResponseDTO> fetchStudijskiProgram() {
        return webClient.get()
                .uri("/studijski-program/")
                .retrieve()
                .bodyToFlux(StudijskiProgramResponseDTO.class);
    }

    private StudijskiProgramRequest createRequest(String naziv, String oznaka, String zvanje, Integer godinaAkreditacije, Integer trajanjeGod, Integer trajanjeSem, Integer espb, Long vrstaStudijaId) {
        StudijskiProgramRequest body = new StudijskiProgramRequest();
        body.setNaziv(naziv);
        body.setOznaka(oznaka);
        body.setGodinaAkreditacije(godinaAkreditacije);
        body.setZvanje(zvanje);
        body.setTrajanjeGodina(trajanjeGod);
        body.setTrajanjeSemestara(trajanjeSem);
        body.setUkupnoEspb(espb);
        body.setVrstaStudija(vrstaStudijaId);

        return body;
    }

    public Mono<Long> createStudijskiProgram(String naziv, String oznaka, String zvanje, Integer godinaAkreditacije, Integer trajanjeGod, Integer trajanjeSem, Integer espb, Long vrstaStudijaId) {
        StudijskiProgramRequest body = this.createRequest(naziv, oznaka, zvanje, godinaAkreditacije, trajanjeGod, trajanjeSem, espb, vrstaStudijaId);

        return webClient.post()
                .uri("/studijski-program/")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<StudijskiProgramResponseDTO> updateStudijskiProgram(Long id, String naziv, String oznaka, String zvanje, Integer godinaAkreditacije, Integer trajanjeGod, Integer trajanjeSem, Integer espb, Long vrstaStudijaId){
        StudijskiProgramRequest body = this.createRequest(naziv, oznaka, zvanje, godinaAkreditacije, trajanjeGod, trajanjeSem, espb, vrstaStudijaId);


        return webClient.patch()
                .uri("/studijski-program/{id}", id)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(StudijskiProgramResponseDTO.class);
    }

    public Mono<Boolean> deleteStudijskiProgram(Long id){
        return webClient.delete()
                .uri("/studijski-program/{id}", id)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Flux<PredmetResponse> fetchStudijskiProgramPredmet(Long studProgramId){
        return webClient.get()
                .uri("/studijski-program/{id}/predmet", studProgramId)
                .retrieve()
                .bodyToFlux(PredmetResponse.class);
    }
}
