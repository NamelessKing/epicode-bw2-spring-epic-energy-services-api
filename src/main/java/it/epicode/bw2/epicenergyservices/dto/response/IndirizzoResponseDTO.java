package it.epicode.bw2.epicenergyservices.dto.response;


public record IndirizzoResponseDTO(
        Long id,
        String via,
        String civico,
        String localita,
        String cap,
        ComuneEmbeddedDTO comune
) {

    public record ComuneEmbeddedDTO(
            Long id,
            String nomeComune
    ) {
    }
}
