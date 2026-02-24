package it.epicode.bw2.epicenergyservices.dto.response;

import jakarta.validation.constraints.NotNull;

public record SedeCliente(long id,
                          @NotNull(message = "Il tipo sede è obbligatorio (LEGALE o OPERATIVA)")
                          TipoSede tipoSede,
                          @NotNull(message = "L'ID del cliente è obbligatorio")
                          Long idCliente,
                          @NotNull(message = "L'ID dell'indirizzo è obbligatorio")
                          Long indirizzoId
) {
}
