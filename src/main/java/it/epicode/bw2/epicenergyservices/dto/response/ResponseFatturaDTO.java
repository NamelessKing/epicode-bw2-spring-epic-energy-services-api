package it.epicode.bw2.epicenergyservices.dto.response;


import java.time.LocalDate;

public record ResponseFatturaDTO(

        LocalDate data,
        double importo,
        long numero,
        Long idCliente,
        Long idUtente,
        String stato
) {
}
