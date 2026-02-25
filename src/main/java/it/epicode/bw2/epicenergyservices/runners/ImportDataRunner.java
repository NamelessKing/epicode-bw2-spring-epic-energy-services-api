package it.epicode.bw2.epicenergyservices.runners;

import it.epicode.bw2.epicenergyservices.repositories.ProvinciaRepository;
import it.epicode.bw2.epicenergyservices.services.ImportAnagraficheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class ImportDataRunner implements CommandLineRunner {

    private final ImportAnagraficheService importAnagraficheService;
    private final ProvinciaRepository provinciaRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== Verifica import CSV Province e Comuni ===");

        // Controlla se il database è già popolato
        long provinceCount = provinciaRepository.count();

        if (provinceCount > 0) {
            log.info("Database già popolato ({} province presenti), skip import CSV", provinceCount);
            return;
        }

        log.info("Database vuoto, avvio import automatico da CSV...");

        try {
            importAnagraficheService.importaTutto();
            log.info("Import CSV completato con successo");
        } catch (Exception e) {
            log.error("Errore durante import CSV", e);
            throw e; // Rilancia per far fallire l'avvio se l'import è critico
        }
    }
}
