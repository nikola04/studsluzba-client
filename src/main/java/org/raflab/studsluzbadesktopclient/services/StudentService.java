package org.raflab.studsluzbadesktopclient.services;

import java.util.List;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbadesktopclient.dtos.PagedResponse;
import org.raflab.studsluzbadesktopclient.dtos.StudentDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
public class StudentService {
	
	private RestTemplate restTemplate;
	private String baseUrl;
	
	private final String STUDENT_URL_PATH = "/api/student";

    private String createURL(String pathEnd) {
		return baseUrl + STUDENT_URL_PATH + "/" + pathEnd;
	}
	private String createURL() {
		return baseUrl + STUDENT_URL_PATH + "/";
	}

	public PagedResponse<StudentDTO> searchStudent(String name, String lastName) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURL("podaci/search"));
		builder.queryParam("name", name);
		builder.queryParam("lastName", lastName);

		ParameterizedTypeReference<PagedResponse<StudentDTO>> responseType =
				new ParameterizedTypeReference<PagedResponse<StudentDTO>>() {};
		ResponseEntity<PagedResponse<StudentDTO>> response =
			restTemplate.exchange(
				builder.toUriString(),
				HttpMethod.GET,
				null,
				responseType
			);

		System.out.println(response.getBody());
		if(response.getStatusCode() == HttpStatus.OK && response.getBody() != null)
			return response.getBody();

		return new PagedResponse<>(List.of(), 0, 0, 0, 0, true);
	}

	public Long saveStudent(StudentDTO student) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURL("podaci"));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<StudentDTO> requestEntity = new HttpEntity<>(student, headers);

		ResponseEntity<Long> response = restTemplate.postForEntity(builder.toUriString(), requestEntity, Long.class);
		if(response.getStatusCode() == HttpStatus.OK && response.getBody() != null)
			return response.getBody();
		else return null;
	}

    public List<StudentDTO> sviStudenti() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURL("podaci"));
        ResponseEntity<StudentDTO[]> response = restTemplate.getForEntity(builder.toUriString(), StudentDTO[].class, HttpMethod.GET);
        if(response.getStatusCode()==HttpStatus.OK)
            return List.of(response.getBody());
        else return null;
    }
}