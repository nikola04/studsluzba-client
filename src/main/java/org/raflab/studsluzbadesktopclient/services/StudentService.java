package org.raflab.studsluzbadesktopclient.services;

import java.util.List;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbadesktopclient.dtos.StudentDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
public class StudentService {
	
	private RestTemplate restTemplate;
	private String baseUrl;
	
	private final String STUDENT_URL_PATH = "/student";

    private String createURL(String pathEnd) {
		return baseUrl + STUDENT_URL_PATH + "/" + pathEnd;
	}

	public List<StudentDTO> searchStudent(String ime) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURL("pronadji"));
		builder.queryParam("ime", ime);
		ResponseEntity<StudentDTO[]> response = restTemplate.getForEntity(builder.toUriString(), StudentDTO[].class, HttpMethod.GET);
		if(response.getStatusCode() == HttpStatus.OK && response.getBody() != null)
			return List.of(response.getBody());
		else return null;
	}

	public Integer saveStudent(StudentDTO student) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURL("add"));
		ResponseEntity<Integer> response = restTemplate.postForEntity(builder.toUriString(), new HttpEntity<>(student), Integer.class);
		if(response.getStatusCode() == HttpStatus.OK && response.getBody() != null)
			return response.getBody();
		else return null;
	}

    public List<StudentDTO> sviStudenti() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURL("all"));
        ResponseEntity<StudentDTO[]> response = restTemplate.getForEntity(builder.toUriString(), StudentDTO[].class, HttpMethod.GET);
        if(response.getStatusCode()==HttpStatus.OK)
            return List.of(response.getBody());
        else return null;
    }
}