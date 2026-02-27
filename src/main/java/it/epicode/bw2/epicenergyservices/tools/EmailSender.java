package it.epicode.bw2.epicenergyservices.tools;


import it.epicode.bw2.epicenergyservices.entities.Cliente;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class EmailSender {
    private String domain;
    private String apiKey;

    public EmailSender(@Value("${mailgun.domain}") String domain,
                       @Value("${mailgun.apiKey}") String apiKey) {
        this.domain = domain;
        this.apiKey = apiKey;
    }

    public void sendRegistration(Cliente recipient) {
        HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domain + "/messages")
                .basicAuth("api", apiKey)
                .queryString("from", "Team 5 <team5@gmail.com>")
                .queryString("to", recipient.getEmail())
                .queryString("subject", "Benvenuto sulla piattaforma")
                .queryString("text", "Ciao " + recipient.getNomeContatto() + ", la tua registrazione è andata a buon fine")
                .asJson();

        System.out.println("Status: " + response.getStatus());
        System.out.println("Body: " + response.getBody());
    }

    /**
     * Invia email custom al contatto del cliente tramite Mailgun
     * @param emailAddress Email destinatario
     * @param subject Soggetto email
     * @param body Corpo email
     */
    public void sendCustomEmail(String emailAddress, String subject, String body) {
        try {
            HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domain + "/messages")
                    .basicAuth("api", apiKey)
                    .queryString("from", "Epic Energy Services <noreply@epicenergy.com>")
                    .queryString("to", emailAddress)
                    .queryString("subject", subject)
                    .queryString("text", body)
                    .asJson();

            if (response.getStatus() == 200) {
                log.info("Email inviata con successo a: {} - Status: {}", emailAddress, response.getStatus());
            } else {
                log.error("Errore nell'invio email a: {} - Status: {} - Body: {}",
                         emailAddress, response.getStatus(), response.getBody());
            }
        } catch (Exception ex) {
            log.error("Exception nell'invio email a: {} - Error: {}", emailAddress, ex.getMessage(), ex);
        }
    }
}

