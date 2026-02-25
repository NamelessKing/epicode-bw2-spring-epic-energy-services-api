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

    @Value("${app.import.enabled:false}")
    private boolean importEnabled;

    @Override
    public void run(String... args) {

        if (!importEnabled) {
            log.info("Import CSV disabilitato (app.import.enabled=false)");
            return;
        }

        long provinceCount = provinciaRepository.count();
        if (provinceCount > 0) {
            log.info("DB già popolato ({} province). Skip import.", provinceCount);
            return;
        }

        log.info("DB vuoto -> import Province + Comuni...");
        importAnagraficheService.importaTutto();
        log.info("Import completato.");
    }
}