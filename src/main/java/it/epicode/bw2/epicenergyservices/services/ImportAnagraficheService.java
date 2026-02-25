package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.entities.Comune;
import it.epicode.bw2.epicenergyservices.entities.Provincia;
import it.epicode.bw2.epicenergyservices.repositories.ComuneRepository;
import it.epicode.bw2.epicenergyservices.repositories.ProvinciaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImportAnagraficheService {

    private final ProvinciaRepository provinciaRepository;
    private final ComuneRepository comuneRepository;

    @Transactional
    public void importaTutto() {
        importaProvince();
        importaComuni();
    }

    @Transactional
    public void importaProvince() {
        try (
                InputStream is = new ClassPathResource("import/province-italiane.csv").getInputStream();
                BOMInputStream bis = new BOMInputStream(is);
                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setDelimiter(';')
                        .setTrim(true)
                        .build()
                        .parse(new InputStreamReader(bis, StandardCharsets.UTF_8))
        ) {
            int riga = 1;

            for (CSVRecord record : parser) {
                riga++;

                String siglaRaw = safeGet(record, "Sigla");
                String provincia = safeGet(record, "Provincia");
                String regione = safeGet(record, "Regione");

                String sigla = normalizeSigla(siglaRaw);

                // Se non riesco ad ottenere 2 lettere -> SKIP
                if (sigla == null) {
                    System.out.println(" SKIP riga " + riga + " (sigla invalida): '" + siglaRaw + "'");
                    continue;
                }

                if (provinciaRepository.existsBySiglaIgnoreCase(sigla)) {
                    continue;
                }

                Provincia p = new Provincia(sigla, provincia, regione);

                try {
                    provinciaRepository.save(p);
                } catch (Exception dbEx) {
                    System.out.println(" ERRORE INSERIMENTO PROVINCIA - riga " + riga);
                    System.out.println("   siglaRaw='" + siglaRaw + "' -> sigla='" + sigla + "'");
                    System.out.println("   provincia='" + provincia + "', regione='" + regione + "'");
                    throw dbEx;
                }
            }

            System.out.println(" Province importate con successo");

        } catch (Exception e) {
            throw new RuntimeException("Errore durante import province", e);
        }
    }

    @Transactional
    public void importaComuni() {

        Map<String, Provincia> provinceMap = new HashMap<>();
        provinciaRepository.findAll().forEach(p ->
                provinceMap.put(p.getProvincia().trim().toUpperCase(Locale.ROOT), p)
        );

        int progressivo = 1;

        try (
                InputStream is = new ClassPathResource("import/comuni-italiani.csv").getInputStream();
                BOMInputStream bis = new BOMInputStream(is);
                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setDelimiter(';')
                        .setTrim(true)
                        .build()
                        .parse(new InputStreamReader(bis, StandardCharsets.UTF_8))
        ) {
            int riga = 1;

            for (CSVRecord record : parser) {
                riga++;

                // questi header DEVONO corrispondere al tuo CSV comuni
                String nomeComune = safeGet(record, "Denominazione in italiano");
                String nomeProvincia = safeGet(record, "Denominazione della provincia").toUpperCase(Locale.ROOT);

                if (nomeComune.isBlank() || nomeProvincia.isBlank()) continue;

                Provincia provincia = provinceMap.get(nomeProvincia.trim().toUpperCase(Locale.ROOT));
                if (provincia == null) {
                    // Se vuoi capire quante province non matchano, scommenta:
                    // System.out.println(" Provincia non trovata per comune riga " + riga + ": " + nomeProvincia);
                    continue;
                }

                Comune comune = new Comune();
                comune.setProgressivoDelComune(progressivo++);
                comune.setNomeComune(nomeComune);
                comune.setProvincia(provincia);

                comuneRepository.save(comune);
            }

            System.out.println(" Comuni importati con successo");

        } catch (Exception e) {
            throw new RuntimeException("Errore durante import comuni", e);
        }
    }

    private String safeGet(CSVRecord record, String header) {
        try {
            String v = record.get(header);
            return v == null ? "" : v.trim();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Normalizza la sigla:
     * - tiene solo lettere
     * - uppercase
     * - se >2 prende le prime 2
     * - se <2 o vuota -> null
     */
    private String normalizeSigla(String raw) {
        if (raw == null) return null;

        // rimuove caratteri non visibili e spazi strani
        String cleaned = raw.replace("\uFEFF", "").trim();

        // tieni solo lettere (es. "RM", "MI", ecc.)
        cleaned = cleaned.replaceAll("[^A-Za-z]", "");

        if (cleaned.isBlank()) return null;

        cleaned = cleaned.toUpperCase(Locale.ROOT);

        if (cleaned.length() < 2) return null;
        if (cleaned.length() > 2) cleaned = cleaned.substring(0, 2);

        return cleaned;
    }
}