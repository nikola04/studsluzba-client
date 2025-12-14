package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    private Long id;
    private String ime;	  // not null
    private String prezime;  // not null
    private String srednjeIme;   // not null
    private String jmbg;
    private Integer godinaUpisa;
    private LocalDate datumRodjenja;  // not null
    private String mestoRodjenja;
    private String mestoPrebivalista;  // not null
    private String drzavaRodjenja;
    private String drzavljanstvo;   // not null
    private String nacionalnost;   // samoizjasnjavanje, moze bilo sta
    private Character pol;    // not null
    private String adresa;  // not null
    private String brojTelefonaMobilni;
    private String brojTelefonaFiksni;
    private String privatniEmail;
    private String fakultetEmail;
    private String brojLicneKarte;
    private String licnuKartuIzdao;
    private String mestoStanovanja;
    private String adresaStanovanja;   // u toku studija
    private Double uspehPrijemni;
    private Double uspehSrednjaSkola;
    private Long srednjaSkolaId;
}
