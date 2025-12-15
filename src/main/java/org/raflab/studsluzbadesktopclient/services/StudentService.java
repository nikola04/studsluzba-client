package org.raflab.studsluzbadesktopclient.services;

import java.util.concurrent.CompletableFuture;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbadesktopclient.dtos.PagedResponse;
import org.raflab.studsluzbadesktopclient.dtos.StudentDTO;
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

	private final String STUDENT_URL_PATH = "student/podaci";

    private String createURL(String pathEnd) {
		return  STUDENT_URL_PATH + "/" + pathEnd;
	}

	private String createSearchUrl(String name, String lastName){
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath(createURL("search"));
		builder.queryParam("name", name);
		builder.queryParam("lastName", lastName);
		System.out.println(builder.toUriString());
		return builder.toUriString();
	}

	public CompletableFuture<PagedResponse<StudentDTO>> searchStudentAsync(String name, String lastName) {
		String url = createSearchUrl(name, lastName);

		return webClient.get()
			.uri(url)
			.retrieve()
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new ServerCommunicationException(clientResponse.statusCode() + "")))
			.bodyToMono(new ParameterizedTypeReference<PagedResponse<StudentDTO>>() {})
				.toFuture();
	}
}