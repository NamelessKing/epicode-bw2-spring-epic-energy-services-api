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

    //Metodo principale richiamato dal Runner.Quindi importo PRIMA  le Province e poi i Comuni
    //perché i Comuni dipendono dalla chiave esterna verso Provincia.

    @Transactional
    public void importaTutto() {
        importaProvince();
        importaComuni();
    }

    // Import Province: viene letto il CSV con header. si evitano così duplicati controllando la sigla
    // poi aggiungo manualmente eventuali province mancanti nel CSV

    @Transactional
    public void importaProvince() {
        try (CSVParser parser = csv("import/province-italiane.csv", true)) {

            int letti = 0, salvati = 0;

            for (CSVRecord r : parser) {
                letti++;

                // "normalizzo" la sigla perché nel CSV può contenere caratteri sporchi
                String sigla = sigla(safe(r.get("Sigla")));

                // Se la sigla non è valida o esiste già nel DB, si salta
                if (sigla == null || provinciaRepository.existsBySiglaIgnoreCase(sigla)) continue;

                String nome = safe(r.get("Provincia"));
                String regione = safe(r.get("Regione"));

                provinciaRepository.save(new Provincia(sigla, nome, regione));
                salvati++;
            }

            //Aggiungo manualmente le province per evitare mismatch
            ensureProvincia("SU", "Sud Sardegna", "Sardegna");
            ensureProvincia("RO", "Rovigo", "Veneto");

            provinciaRepository.flush();

            System.out.println("Province - Letti: " + letti);
            System.out.println("Province - Salvati: " + salvati);
            System.out.println("Province - Count DB: " + provinciaRepository.count());

        } catch (Exception e) {
            throw new RuntimeException("Errore durante import province", e);
        }
    }

    //Import Comuni: si legge il CSV per indice (l'header non è affidabile). Per evitare mismatch,
    // creo una mappa normalizzata che invece di essere nomeProvinciaNormalizzato DIVENTA Provincia

    @Transactional
    public void importaComuni() {

        // creo una mappa per trovare velocemente la Provinci, evitando query DB ripetute dentro il ciclo
        Map<String, Provincia> byKey = new HashMap<>();
        provinciaRepository.findAll()
                .forEach(p -> byKey.put(key(p.getProvincia()), p));

        // Inserisco a mano degli Alias minimi per i casi in cui il nome nel CSV comuni
         // è diverso dal nome nel CSV province, giusto i più complicati.

        Map<String, String> alias = Map.of(
                key("Verbano-Cusio-Ossola"), key("Verbania"),
                key("Valle d'Aosta/Vallée d'Aoste"), key("Aosta"),
                key("Monza e della Brianza"), key("Monza-Brianza"),
                key("Bolzano/Bozen"), key("Bolzano"),
                key("Reggio nell'Emilia"), key("Reggio-Emilia"),
                key("Pesaro e Urbino"), key("Pesaro-Urbino")
        );

        int letti = 0, salvati = 0, scartati = 0, nonTrovata = 0;

        try (CSVParser parser = csv("import/comuni-italiani.csv", false)) {

            for (CSVRecord r : parser) {
                letti++;

                // Ci servono almeno 4 colonne: codProv, progressivo, nomeComune, nomeProvincia
                if (r.size() < 4) { scartati++; continue; }

                int progressivo = toInt(safe(r.get(1)));
                String nomeComune = safe(r.get(2));
                String rawProv = safe(r.get(3));

                // Se dati fondamentali mancanti, scartiamo la riga
                if (progressivo <= 0 || nomeComune.isBlank() || rawProv.isBlank()) {
                    scartati++;
                    continue;
                }

                // normalizzo il nome provincia
                String k = key(rawProv);

                // Se esiste alias, lo traduco
                k = alias.getOrDefault(k, k);

                Provincia provincia = byKey.get(k);

                // Se non trovo la provincia, non rompiamo l'import:
                // semplicemente contiamo il caso e andiamo avanti
                if (provincia == null) { nonTrovata++; continue; }

                comuneRepository.save(new Comune(progressivo, nomeComune, provincia));
                salvati++;
            }

            comuneRepository.flush();

            System.out.println("Comuni - Letti: " + letti);
            System.out.println("Comuni - Salvati: " + salvati);
            System.out.println("Comuni - Scartati: " + scartati);
            System.out.println("Comuni - Provincia non trovata: " + nonTrovata);
            System.out.println("Comuni - Count DB: " + comuneRepository.count());

        } catch (Exception e) {
            throw new RuntimeException("Errore durante import comuni", e);
        }
    }

    // ==============================
    // Helpers
    // ==============================


    //Creo un parser CSV che rileva e rimuove i BOM(è una sequenza speciale di byte cioè UTF-8, UTF-16, UTF-32)
    // per evitare problemi nella lettura della prima intestazione
  // così da usare i punti e virgola ';' come separatore dei campi

    private CSVParser csv(String path, boolean header) throws Exception {
        InputStream is = new ClassPathResource(path).getInputStream();
        BOMInputStream bis = new BOMInputStream(is);

        CSVFormat.Builder b = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .setTrim(true);

        if (header) b.setHeader().setSkipHeaderRecord(true);
        else b.setSkipHeaderRecord(true);

        return b.build().parse(new InputStreamReader(bis, StandardCharsets.UTF_8));
    }

    //qua inserisco una provincia solo se non esiste già. Usato per compensare CSV incompleti.

    private void ensureProvincia(String sigla, String provincia, String regione) {
        if (!provinciaRepository.existsBySiglaIgnoreCase(sigla)) {
            provinciaRepository.save(new Provincia(sigla, provincia, regione));
        }
    }

    //Pulisco stringhe da BOM e spazi inutili.

    private String safe(String s) {
        return s == null ? "" : s.replace("\uFEFF", "").trim();
    }

    //  se errore ritorna 0.
    private int toInt(String s) {
        try { return s.isBlank() ? 0 : Integer.parseInt(s); }
        catch (Exception e) { return 0; }
    }

    //Normalizza la sigla provincia: solo lettere, massimo 2 caratteri e poi uppercase DP
    private String sigla(String raw) {
        String s = raw.replaceAll("[^A-Za-z]", "").toUpperCase(Locale.ROOT);
        if (s.length() < 2) return null;
        return s.length() > 2 ? s.substring(0, 2) : s;
    }

    // ALTRA Normalizzazione  per confrontare nomi provincia:metto uppercase, rimozione accenti
     // rimozione spazi, trattini, slash rimozione apostrofi
    // Serve per evitare mismatch tra CSV diversi.

    private String key(String raw) {
        String s = safe(raw).toUpperCase(Locale.ROOT)
                .replace("’", "'").replace("`", "'")
                .replace("-", " ").replace("/", " ");

        s = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return s.replace("'", "").replaceAll("\\s+", "");
    }
}