package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.request.StudentIndeksRequest;
import org.raflab.studsluzbacommon.dto.request.UplataRequest;
import org.raflab.studsluzbacommon.dto.response.*;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.exceptions.ResourceNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.naming.CommunicationException;
import java.time.LocalDate;

@Service
@AllArgsConstructor
public class StudentIndexService {

	private WebClient webClient;

    private String createURL(String pathEnd) {
		return "student/indeks/" + pathEnd;
	}

	public Mono<StudentIndeksResponseDTO> fetchStudentIndexByIndexNumber(String index){
		return webClient.get()
			.uri(createURL(index))
			.retrieve()
			.onStatus(status -> status.value() == 400, clientResponse ->
					Mono.error(new InvalidDataException("Indeks Broj is not valid. Use [SP][Broj][Godina].")))
			.onStatus(status -> status.value() == 404, clientResponse ->
					Mono.error(new ResourceNotFoundException("Student with provided indeks cannot be found.")))
			.bodyToMono(new ParameterizedTypeReference<>() {});
	}

	public Flux<StudentIndeksResponseDTO> fetchStudentIndexByStudentId(Long studentId){
		return webClient.get()
			.uri("student/podaci/{id}/indeks", studentId)
			.retrieve()
			.bodyToFlux(StudentIndeksResponseDTO.class);
	}

	public Mono<Long> createStudentIndex(Long studentId, Integer godina, Long studProgramId, Long nacinFinansiranjaId, Boolean aktivan, LocalDate vaziOd){
		StudentIndeksRequest body = new StudentIndeksRequest();
		body.setGodina(godina);
		body.setStudProgramId(studProgramId);
		body.setNacinFinansiranjaId(nacinFinansiranjaId);
		body.setAktivan(aktivan);
		body.setVaziOd(vaziOd);

		return webClient.post()
			.uri("student/podaci/{id}/indeks", studentId)
			.bodyValue(body)
			.retrieve()
			.bodyToMono(Long.class);
	}

	public Mono<Boolean> deleteStudentIndexById(Long studentIndexId){
		return webClient.delete()
			.uri("student/indeks/{id}", studentIndexId)
			.retrieve()
			.bodyToMono(Boolean.class);
	}

	public Mono<Double> findStudentAverageOcena(Long indexId){
		return webClient.get()
			.uri(createURL(indexId + "/predmet/polozen/average-ocena"))
			.retrieve()
			.onStatus(status -> status.value() == 404, clientResponse ->
					Mono.error(new ResourceNotFoundException("Student with provided indeks cannot be found.")))
			.onStatus(HttpStatusCode::isError, clientResponse ->
					Mono.error(new CommunicationException(clientResponse.statusCode().toString())))
			.bodyToMono(Double.class);
	}

	public Flux<UplataResponse> fetchStudentUplata(Long indexId){
		return webClient.get()
				.uri(createURL(indexId + "/uplata"))
				.retrieve()
				.bodyToFlux(UplataResponse.class);
	}

	public Mono<UplataResponse> fetchStudentUplata(Long indexId, Long uplataId){
		return webClient.get()
				.uri(createURL(indexId + "/uplata/" + uplataId))
				.retrieve()
				.onStatus(status -> status.value() == 404, clientResponse ->
						Mono.error(new ResourceNotFoundException("Uplata cannot be found.")))
				.bodyToMono(UplataResponse.class);
	}

	public Mono<Long> createStudentUplata(Long indexId, Double amount){
		UplataRequest body = new UplataRequest();
		body.setIznos(amount);

		return webClient.post()
				.uri(createURL(indexId + "/uplata"))
				.bodyValue(body)
				.retrieve()
				.onStatus(status -> status.value() == 400, clientResponse ->
						Mono.error(new ResourceNotFoundException("Amount is not valid.")))
				.bodyToMono(Long.class);
	}

	public Mono<Boolean> deleteStudentUplata(Long indexId, Long uplataId){
		return webClient.delete()
				.uri(createURL(indexId + "/uplata/" + uplataId))
				.retrieve()
				.onStatus(status -> status.value() == 404, clientResponse ->
						Mono.error(new ResourceNotFoundException("Uplata cannot be found.")))
				.toBodilessEntity()
				.thenReturn(true);
	}

	public Mono<IznosResponse> fetchUplataPreostaliIznos(Long indexId){
		return webClient.get()
				.uri(createURL(indexId + "/uplata/preostalo"))
				.retrieve()
				.bodyToMono(IznosResponse.class);
	}

	public Flux<UpisGodineResponse> fetchStudentUpisGodine(Long indexId){
		return webClient.get()
				.uri(createURL(indexId + "/upis"))
				.retrieve()
				.bodyToFlux(UpisGodineResponse.class);
	}

	public Mono<Long> createStudentUpisGodine(Long indexId, Long skolskaGodinaId, String note){
		return webClient.post()
				.uri(uriBuilder -> uriBuilder
						.path(createURL(indexId + "/upis/godina/" + skolskaGodinaId))
						.queryParam("napomena", note)
						.build())
				.bodyValue("")
				.retrieve()
				.bodyToMono(Long.class);
	}

	public Flux<ObnovaGodineResponse> fetchStudentObnovaGodine(Long indexId){
		return webClient.get()
				.uri(createURL(indexId + "/obnova"))
				.retrieve()
				.bodyToFlux(ObnovaGodineResponse.class);
	}

	public Mono<Long> createStudentObnovaGodine(Long indexId, Long skolskaGodinaId, String note){
		return webClient.post()
				.uri(uriBuilder -> uriBuilder
						.path(createURL(indexId + "/obnova/godina/" + skolskaGodinaId))
						.queryParam("napomena", note)
						.build())
				.retrieve()
				.bodyToMono(Long.class);
	}

	public Flux<IspitResponse> fetchStudentPolozenIspit(Long indexId){
		return webClient.get()
				.uri(createURL(indexId + "/ispit/polozen"))
				.retrieve()
				.bodyToFlux(IspitResponse.class);
	}

	public Flux<IspitResponse> fetchStudentNepolozeniIspiti(Long indexId){
		return webClient.get()
				.uri(createURL(indexId + "/ispit/nepolozen"))
				.retrieve()
				.bodyToFlux(IspitResponse.class);
	}
}