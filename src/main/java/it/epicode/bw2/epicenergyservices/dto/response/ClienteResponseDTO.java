package it.epicode.bw2.epicenergyservices.dto.response;

import it.epicode.bw2.epicenergyservices.entities.TipoAzienda;

import java.time.LocalDate;


public record ClienteResponseDTO(
        Long id,
        String logoAziendale,
        String ragioneSociale,
        String partitaIva,
        String email,
        double fatturatoAnnuale,
        String pec,
        String telefono,
        TipoAzienda tipo,
        LocalDate dataInserimento,
        LocalDate dataUltimoContatto,
        String emailContatto,
        String nomeContatto,
        String cognomeContatto,
        String telefonoContatto,
        Long idSedeLegale,
        Long idSedeOperativa
) {
}
