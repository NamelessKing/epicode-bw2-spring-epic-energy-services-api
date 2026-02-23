package it.epicode.bw2.epicenergyservices.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurazione OpenAPI/Swagger
 * 
 * Responsabilità:
 * - Configura la documentazione API (Swagger UI)
 * - Definisce le informazioni del progetto
 * - Configura schema di sicurezza JWT
 * 
 * Accesso:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenAPIConfig {
    
    /**
     * Bean per configurazione OpenAPI personalizzata
     * 
     * Configura:
     * - Titolo e versione API
     * - Descrizione progetto
     * - Contatti team
     * - Informazioni licenza
     * - Schema di sicurezza JWT (Bearer token)
     * 
     * @return OpenAPI bean configurato
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            // ==================== INFO ====================
            .info(new Info()
                .title("EPIC ENERGY SERVICES - Backend API")
                .version("1.0.0")
                .description("Backend REST API per il sistema CRM di EPIC ENERGY SERVICES\n\n" +
                    "Azienda fornitrice di energia che gestisce clienti business.\n\n" +
                    "**Stack Tecnologico:**\n" +
                    "- Spring Boot 4.0.3\n" +
                    "- PostgreSQL Database\n" +
                    "- JWT Token Authentication\n" +
                    "- BCrypt Password Encoding\n\n" +
                    "**Autenticazione:**\n" +
                    "1. POST /auth/login per ottenere JWT token\n" +
                    "2. Aggiungi token negli header: Authorization: Bearer <token>\n" +
                    "3. Token valido per 7 giorni\n\n" +
                    "**Ruoli:**\n" +
                    "- USER: accesso limitato (letture + inserimento clienti)\n" +
                    "- ADMIN: accesso completo (tutte le operazioni)"
                )
                .contact(new Contact()
                    .name("Team Build Week 2 - Epicode")
                    .email("support@epicenergy.it")
                    .url("https://epicenergy.it")
                )
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")
                )
            )
            
            // ==================== SECURITY SCHEME ====================
            .components(new Components()
                // Definisce lo schema di sicurezza JWT Bearer
                .addSecuritySchemes("Bearer Authentication",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Inserisci il JWT token ricevuto da POST /auth/login\n\n" +
                            "Esempio:\n" +
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJFcGljRW5lcmd5IiwiZXhwIjoxNjkzODk5MjAwLCJpYXQiOjE2OTM4OTkxMDB9.signature\n\n" +
                            "Formato: Authorization: Bearer <token>"
                        )
                )
            );
    }
}
