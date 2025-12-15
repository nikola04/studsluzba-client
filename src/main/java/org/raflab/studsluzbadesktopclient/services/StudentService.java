package org.raflab.studsluzbadesktopclient.services;

import java.util.concurrent.CompletableFuture;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.PagedResponse;
import org.raflab.studsluzbacommon.dto.response.StudentResponseDTO;
import org.raflab.studsluzbadesktopclient.exceptions.ServerCommunicationException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class StudentService {

	private WebClient webClient;

    private String createURL(String pathEnd) {
		return "student/podaci/" + pathEnd;
	}

	private String createSearchUrl(String name, String lastName){
		if (name == null || lastName == null || (name.isEmpty() && lastName.isEmpty()))
			return createURL("");

		UriComponentsBuilder builder = UriComponentsBuilder.fromPath(createURL("search"));
		builder.queryParam("name", name);
		builder.queryParam("lastName", lastName);
		return builder.toUriString();
	}

	public CompletableFuture<PagedResponse<StudentResponseDTO>> searchStudentAsync(String name, String lastName) {
		String url = createSearchUrl(name, lastName);

		return webClient.get()
			.uri(url)
			.retrieve()
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new ServerCommunicationException(clientResponse.statusCode().toString())))
			.bodyToMono(new ParameterizedTypeReference<PagedResponse<StudentResponseDTO>>() {})
				.toFuture();
	}
}