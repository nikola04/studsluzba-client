package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbacommon.dto.PagedResponse;
import org.raflab.studsluzbacommon.dto.request.StudentRequest;
import org.raflab.studsluzbacommon.dto.response.StudentResponseDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class StudentService {

	private WebClient webClient;

    private String createURL(String pathEnd) {
		return "student/podaci/" + pathEnd;
	}

	private String createSearchUrl(String name, String lastName, String highSchoolName){
		if (name == null || lastName == null || highSchoolName == null || (name.isEmpty() && lastName.isEmpty() && highSchoolName.isEmpty()))
			return createURL("");

		UriComponentsBuilder builder = UriComponentsBuilder.fromPath(createURL("search"));
		builder.queryParam("name", name);
		builder.queryParam("lastName", lastName);
		builder.queryParam("highSchoolName", highSchoolName);
		return builder.toUriString();
	}

	public Mono<PagedResponse<StudentResponseDTO>> searchStudents(String name, String lastName, String highSchoolName) {
		String url = createSearchUrl(name, lastName, highSchoolName);

		return webClient.get()
			.uri(url)
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<>() {});
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
			String mestoPrebivalista,
			String drzavaRodjenja,
			String drzavljanstvo,
			String nacionalnost,
			Character pol,
			String adresa,
			String mobilni,
			String fiksni,
			String fakultetEmail,
			String privatniEmail,
			String brojLicneKarte,
			String licnuKartuIzdao,
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
		body.setMestoPrebivalista(mestoPrebivalista);
		body.setDrzavaRodjenja(drzavaRodjenja);
		body.setDrzavljanstvo(drzavljanstvo);
		body.setNacionalnost(nacionalnost);
		body.setPol(pol);

		body.setAdresa(adresa);
		body.setBrojTelefonaMobilni(mobilni);
		body.setBrojTelefonaFiksni(fiksni);
		body.setFakultetEmail(fakultetEmail);
		body.setPrivatniEmail(privatniEmail);

		body.setBrojLicneKarte(brojLicneKarte);
		body.setLicnuKartuIzdao(licnuKartuIzdao);
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