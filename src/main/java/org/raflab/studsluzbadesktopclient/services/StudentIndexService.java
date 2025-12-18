package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.response.StudentIndeksResponseDTO;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.exceptions.ResourceNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.CommunicationException;

@Service
@AllArgsConstructor
public class StudentIndexService {

	private WebClient webClient;

    private String createURL(String pathEnd) {
		return "student/indeks/" + pathEnd;
	}

	public Mono<StudentIndeksResponseDTO> findStudentIndexByIndexNumber(String index){
		return webClient.get()
			.uri(createURL(index))
			.retrieve()
			.onStatus(status -> status.value() == 400, clientResponse ->
					Mono.error(new InvalidDataException("Indeks Broj is not valid. Use [SP][Broj][Godina].")))
			.onStatus(status -> status.value() == 404, clientResponse ->
					Mono.error(new ResourceNotFoundException("Student with provided indeks cannot be found.")))
			.onStatus(HttpStatusCode::isError, clientResponse ->
					Mono.error(new CommunicationException(clientResponse.statusCode().toString())))
			.bodyToMono(new ParameterizedTypeReference<>() {});
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
}