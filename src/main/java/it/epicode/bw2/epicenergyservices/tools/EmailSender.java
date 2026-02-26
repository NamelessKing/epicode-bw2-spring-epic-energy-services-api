package it.epicode.bw2.epicenergyservices.tools;


import it.epicode.bw2.epicenergyservices.entities.Cliente;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
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
}

