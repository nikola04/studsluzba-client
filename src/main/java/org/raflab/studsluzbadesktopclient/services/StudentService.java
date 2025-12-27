package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.PagedResponse;
import org.raflab.studsluzbacommon.dto.request.StudentRequest;
import org.raflab.studsluzbacommon.dto.response.StudentResponseDTO;
import org.raflab.studsluzbadesktopclient.exceptions.ResourceNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class StudentService {

	private WebClient webClient;

    private String createURL(String pathEnd) {
		return "student/podaci/" + pathEnd;
	}

	public Mono<PagedResponse<StudentResponseDTO>> searchStudents(String name, String lastName, String highSchoolName, Integer page, Integer size) {
		return webClient.get()
			.uri(uriBuilder -> uriBuilder
					.path(createURL("search"))
					.queryParam("name", name)
					.queryParam("lastName", lastName)
					.queryParam("highSchoolName", highSchoolName)
					.queryParam("page", page)
					.queryParam("size", size)
					.build())
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<>() {});
	}

	public Mono<Long> saveStudent(StudentRequest student) {
		return webClient.post()
				.uri("student/podaci")
				.bodyValue(student)
				.retrieve()
				.bodyToMono(Long.class);
	}

	public Mono<Boolean> deleteStudent(Long studentId){
		return webClient.delete()
				.uri("student/podaci/{id}", studentId)
				.retrieve()
				.onStatus(status -> status.value() == 404, clientResponse -> Mono.error(new ResourceNotFoundException("Student cannot be found.")))
				.bodyToMono(Boolean.class);
	}

	public Mono<StudentResponseDTO> updateStudentById(
			Long id,
			String firstName,
			String lastName,
			String middleName,
			String jmbg,
			Integer godinaUpisa,
			LocalDate datumRodjenja,
			String mestoRodjenja,
			String drzavaRodjenja,
			String drzavljanstvo,
			String nacionalnost,
			Character pol,
			String mobilni,
			String fakultetEmail,
			String privatniEmail,
			String brojLicneKarte,
			String mestoStanovanja,
			String adresaStanovanja,
			Double uspehSrednja,
			Double uspehPrijemni,
			Long schoolId,
			Long universityId) {

		StudentRequest body = new StudentRequest();

		body.setIme(firstName);
		body.setPrezime(lastName);
		body.setSrednjeIme(middleName);
		body.setJmbg(jmbg);
		body.setGodinaUpisa(godinaUpisa);

		body.setDatumRodjenja(datumRodjenja);
		body.setMestoRodjenja(mestoRodjenja);
		body.setDrzavaRodjenja(drzavaRodjenja);
		body.setDrzavljanstvo(drzavljanstvo);
		body.setNacionalnost(nacionalnost);
		body.setPol(pol);

		body.setBrojTelefonaMobilni(mobilni);
		body.setFakultetEmail(fakultetEmail);
		body.setPrivatniEmail(privatniEmail);

		body.setBrojLicneKarte(brojLicneKarte);
		body.setMestoStanovanja(mestoStanovanja);
		body.setAdresaStanovanja(adresaStanovanja);

		body.setUspehSrednjaSkola(uspehSrednja);
		body.setUspehPrijemni(uspehPrijemni);
		body.setSrednjaSkolaId(schoolId);
		body.setVisokoskolskaUstanovaId(universityId);

		return webClient.patch()
				.uri(createURL(id.toString()))
				.bodyValue(body)
				.retrieve()
				.bodyToMono(StudentResponseDTO.class);
	}
}