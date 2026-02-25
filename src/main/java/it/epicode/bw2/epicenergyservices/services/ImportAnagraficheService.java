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
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImportAnagraficheService {

    private final ProvinciaRepository provinciaRepository;
    private final ComuneRepository comuneRepository;

    /**
     * Entry point: importa prima le Province e poi i Comuni.
     * Serve farlo in quest’ordine perché Comune ha una FK verso Provincia.
     */
    @Transactional
    public void importaTutto() {
        importaProvince();
        importaComuni();
    }

    /**
     * Import Province:
     * - legge province-italiane.csv (ha header affidabile)
     * - normalizza la sigla a 2 lettere
     * - evita duplicati su sigla
     */
    @Transactional
    public void importaProvince() {
        try (
                InputStream is = new ClassPathResource("import/province-italiane.csv").getInputStream();
                BOMInputStream bis = new BOMInputStream(is);
                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()                 // qui va bene: l’header è corretto
                        .setSkipHeaderRecord(true)
                        .setDelimiter(';')
                        .setTrim(true)
                        .build()
                        .parse(new InputStreamReader(bis, StandardCharsets.UTF_8))
        ) {
            int riga = 1;
            int letti = 0;
            int salvati = 0;
            int skipSiglaInvalida = 0;
            int skipDuplicati = 0;

            for (CSVRecord record : parser) {
                riga++;
                letti++;

                String siglaRaw = safeGet(record, "Sigla");
                String provincia = safeGet(record, "Provincia");
                String regione = safeGet(record, "Regione");

                String sigla = normalizeSigla(siglaRaw);

                // Se la sigla non diventa "XX" (2 lettere), la riga non è valida
                if (sigla == null) {
                    skipSiglaInvalida++;
                    continue;
                }

                // Se esiste già, non reinserire
                if (provinciaRepository.existsBySiglaIgnoreCase(sigla)) {
                    skipDuplicati++;
                    continue;
                }

                provinciaRepository.save(new Provincia(sigla, provincia, regione));
                salvati++;
            }

            // Flush: se c'è qualche problema DB, lo scopriamo subito qui
            provinciaRepository.flush();

            System.out.println("Province importate con successo");
            System.out.println("Province - Letti: " + letti);
            System.out.println("Province - Salvati: " + salvati);
            System.out.println("Province - Skip sigla invalida: " + skipSiglaInvalida);
            System.out.println("Province - Skip duplicati: " + skipDuplicati);
            System.out.println("Province - Count DB: " + provinciaRepository.count());

        } catch (Exception e) {
            throw new RuntimeException("Errore durante import province", e);
        }
    }

    /**
     * Import Comuni:
     *
     * Nota IMPORTANTISSIMA: il file comuni-italiani.csv ha un header “incompleto”
     * (3 colonne) ma le righe dati sono a 4 colonne:
     *
     * 0 = codice provincia (storico)
     * 1 = progressivo comune
     * 2 = nome comune
     * 3 = nome provincia
     *
     * Quindi qui NON usiamo record.get("nomeColonna") ma record.get(indice).
     *
     * Inoltre: i nomi provincia tra i due CSV non sono sempre identici (trattini, apostrofi, accenti).
     * Per questo creiamo una mappa "chiave normalizzata" -> Provincia.
     */
    @Transactional
    public void importaComuni() {

        // 1) Carico tutte le province dal DB e creo una mappa per trovarle velocemente
        Map<String, Provincia> provinceMap = new HashMap<>();
        provinciaRepository.findAll().forEach(p ->
                provinceMap.put(normalizeProvinciaKey(p.getProvincia()), p)
        );

        int riga = 1;
        int letti = 0;
        int salvati = 0;
        int scartatiFormatoRiga = 0;
        int scartatiCampiVuoti = 0;
        int scartatiProvinciaNonTrovata = 0;

        try (
                InputStream is = new ClassPathResource("import/comuni-italiani.csv").getInputStream();
                BOMInputStream bis = new BOMInputStream(is);
                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setSkipHeaderRecord(true)   // salta la prima riga header
                        .setDelimiter(';')
                        .setTrim(true)
                        .build()
                        .parse(new InputStreamReader(bis, StandardCharsets.UTF_8))
        ) {
            for (CSVRecord record : parser) {
                riga++;
                letti++;

                // Se una riga non ha almeno 4 colonne, non possiamo leggerla correttamente
                if (record.size() < 4) {
                    scartatiFormatoRiga++;
                    continue;
                }

                // Leggiamo per indice (vedi commento sopra)
                String progressivoCsv = record.get(1) == null ? "" : record.get(1).trim();
                String nomeComune = record.get(2) == null ? "" : record.get(2).trim();
                String nomeProvinciaRaw = record.get(3) == null ? "" : record.get(3).trim();

                if (nomeComune.isBlank() || nomeProvinciaRaw.isBlank()) {
                    scartatiCampiVuoti++;
                    continue;
                }

                // Cerco la provincia nel DB usando una chiave normalizzata
                Provincia provincia = provinceMap.get(normalizeProvinciaKey(nomeProvinciaRaw));
                if (provincia == null) {
                    scartatiProvinciaNonTrovata++;
                    continue;
                }

                // Progressivo: lo convertiamo in int. Se non è valido, scartiamo (è un dato “chiave” del record)
                int progressivoDelComune = parseInt(progressivoCsv);
                if (progressivoDelComune <= 0) {
                    scartatiCampiVuoti++;
                    continue;
                }

                Comune comune = new Comune();
                comune.setProgressivoDelComune(progressivoDelComune);
                comune.setNomeComune(nomeComune);
                comune.setProvincia(provincia);

                comuneRepository.save(comune);
                salvati++;
            }

            // Flush: se c’è un errore (vincoli/validazione), lo scopri qui e non “alla fine”
            comuneRepository.flush();

            System.out.println("Comuni importati con successo");
            System.out.println("Comuni - Letti: " + letti);
            System.out.println("Comuni - Salvati: " + salvati);
            System.out.println("Comuni - Scartati formato riga: " + scartatiFormatoRiga);
            System.out.println("Comuni - Scartati campi vuoti/invalidi: " + scartatiCampiVuoti);
            System.out.println("Comuni - Scartati provincia non trovata: " + scartatiProvinciaNonTrovata);
            System.out.println("Comuni - Province in mappa: " + provinceMap.size());
            System.out.println("Comuni - Count DB: " + comuneRepository.count());

        } catch (Exception e) {
            throw new RuntimeException("Errore durante import comuni (riga " + riga + ")", e);
        }
    }

    // =========================
    // Helpers “semplici”
    // =========================

    private String safeGet(CSVRecord record, String header) {
        try {
            String v = record.get(header);
            return v == null ? "" : v.trim();
        } catch (Exception e) {
            return "";
        }
    }

    private int parseInt(String raw) {
        try {
            if (raw == null) return 0;
            String cleaned = raw.replace("\uFEFF", "").trim();
            if (cleaned.isBlank()) return 0;
            return Integer.parseInt(cleaned);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Sigla provincia: vogliamo sempre 2 lettere (RM, MI, NA, ...)
     * Se non ci riusciamo -> null (riga non valida).
     */
    private String normalizeSigla(String raw) {
        if (raw == null) return null;

        String cleaned = raw.replace("\uFEFF", "").trim();
        cleaned = cleaned.replaceAll("[^A-Za-z]", ""); // solo lettere

        if (cleaned.isBlank()) return null;

        cleaned = cleaned.toUpperCase(Locale.ROOT);

        if (cleaned.length() < 2) return null;
        if (cleaned.length() > 2) cleaned = cleaned.substring(0, 2);

        return cleaned;
    }

    /**
     * Normalizza un nome provincia per matchare due CSV diversi.
     * Obiettivo: rendere uguali stringhe che “sembrano uguali” ma sono scritte diversamente.
     *
     * Esempi:
     * - "Ascoli-Piceno"  -> "ASCOLIPICENO"
     * - "L'Aquila"       -> "LAQUILA"
     * - "Forlì-Cesena"   -> "FORLICESENA"
     */
    private String normalizeProvinciaKey(String raw) {
        if (raw == null) return "";

        String s = raw.replace("\uFEFF", "").trim().toUpperCase(Locale.ROOT);

        // uniformo apostrofi e separatori
        s = s.replace("’", "'").replace("`", "'");
        s = s.replace("-", " ").replace("/", " ");

        // tolgo gli accenti (FORLÌ -> FORLI)
        s = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // rimuovo spazi e apostrofi per avere una chiave “compatta”
        s = s.replace("'", "");
        s = s.replaceAll("\\s+", "");

        return s;
    }
}
