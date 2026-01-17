package org.raflab.studsluzbadesktopclient.models;

import lombok.Getter;
import lombok.Setter;
import org.raflab.studsluzbacommon.dto.response.NastavnikResponseDTO;
import org.raflab.studsluzbacommon.dto.response.PolozenPredmetResponse;

import java.time.format.DateTimeFormatter;
@Getter
@Setter
public class IspitReportBean {
    private String nazivPredmeta;
    private Integer espb;
    private Integer ocena;
    private String datumPolaganja;
    private Integer godinaStudija;
    private String nastavnik;

    public IspitReportBean(PolozenPredmetResponse pp) {
        this.nazivPredmeta = pp.getPredmet().getNaziv();
        this.espb = pp.getPredmet().getEspb();
        this.ocena = pp.getOcena();

        if (pp.getIspitIzlazak() != null &&
                pp.getIspitIzlazak().getIspitPrijava() != null &&
                pp.getIspitIzlazak().getIspitPrijava().getIspit() != null) {

            var ispit = pp.getIspitIzlazak().getIspitPrijava().getIspit();
            this.datumPolaganja = ispit.getDatumOdrzavanja().format(DateTimeFormatter.ofPattern("dd.MM.yyyy."));
            this.godinaStudija = ispit.getIspitniRok().getSkolskaGodina().getGodina();
            NastavnikResponseDTO nastavnikResponse = pp.getIspitIzlazak().getIspitPrijava().getIspit().getNastavnik();
            this.nastavnik = nastavnikResponse.getIme() + " " + nastavnikResponse.getPrezime();
        } else {
            this.datumPolaganja = "Priznat";
            this.godinaStudija = 0;
            this.nastavnik = "N/A";
        }
    }
}