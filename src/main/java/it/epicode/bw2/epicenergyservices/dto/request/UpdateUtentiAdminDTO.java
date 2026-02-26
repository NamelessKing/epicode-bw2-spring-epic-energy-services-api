package it.epicode.bw2.epicenergyservices.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUtentiAdminDTO(@Size(min = 3, max = 50, message = "Username deve essere tra 3 e 50 caratteri")
                                   String username,
                                   @Email(message = "Email non valida")
                                   String email,
                                   @Size(min = 6, message = "Password deve avere almeno 6 caratteri")
                                   String password,
                                   String firstName,
                                   String lastName) {
}
