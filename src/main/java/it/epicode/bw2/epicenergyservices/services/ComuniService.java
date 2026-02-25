package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.ComuneRequestDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ComuneResponseDTO;
import it.epicode.bw2.epicenergyservices.entities.Comune;
import it.epicode.bw2.epicenergyservices.entities.Provincia;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.ConflictException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.ComuneRepository;
import it.epicode.bw2.epicenergyservices.repositories.IndirizziRepository;
import it.epicode.bw2.epicenergyservices.repositories.ProvinciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComuniService {

    private final ComuneRepository comuneRepository;
    private final ProvinceService provinceService;
    private final ProvinciaRepository provinciaRepository;
    private final IndirizziRepository indirizziRepository;

    // Campi validi per ordinamento
    private static final List<String> VALID_SORT_FIELDS = Arrays.asList(
            "id", "nomeComune", "progressivoDelComune"
    );

    public Page<ComuneResponseDTO> findAll(int page, int size, String orderBy) {

        // Validazione parametri
        if (size > 200 || size <= 0) {
            log.warn("Size non valido ({}), impostato a default: 10", size);
            size = 10;
        }
        if (page < 0) {
            log.warn("Page non valido ({}), impostato a default: 0", page);
            page = 0;
        }

        // Validazione orderBy - previene errori e SQL injection
        if (orderBy == null || orderBy.isBlank() || !VALID_SORT_FIELDS.contains(orderBy)) {
            if (orderBy != null && !orderBy.isBlank()) {
                log.warn("Campo ordinamento '{}' non valido. Campi validi: {}. Uso default 'nomeComune'",
                        orderBy, VALID_SORT_FIELDS);
            }
            orderBy = "nomeComune";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));

        return comuneRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    public ComuneResponseDTO saveComune(ComuneRequestDTO payload) {

        if (comuneRepository.existsByProgressivoDelComune(payload.progressivoDelComune())) {
            throw new BadRequestException("Esiste già un comune con progressivo: "
                    + payload.progressivoDelComune());
        }

        Provincia provincia = provinciaRepository.findById(payload.provinciaId())
                .orElseThrow(() ->
                        new NotFoundException("Provincia con id " + payload.provinciaId() + " non trovata"));

        Comune comune = new Comune(
                payload.progressivoDelComune(),
                payload.nomeComune(), // già trimmed dal compact constructor
                provincia
        );

        Comune saved = comuneRepository.save(comune);
        log.info("Comune creato: ID={}, Nome={}", saved.getId(), saved.getNomeComune());

        return convertToResponseDTO(saved);
    }

    public Comune findById(long comuneId) {

        return comuneRepository.findById(comuneId)
                .orElseThrow(() ->
                        new NotFoundException("Comune con id " + comuneId + " non trovato"));
    }

    public ComuneResponseDTO findByIdAndUpdate(long comuneId, ComuneRequestDTO payload) {

        Comune found = comuneRepository.findById(comuneId)
                .orElseThrow(() ->
                        new NotFoundException("Comune con id " + comuneId + " non trovato"));

        // Verifica se il progressivo cambia e se è già in uso
        if (found.getProgressivoDelComune() != payload.progressivoDelComune()) {
            if (comuneRepository.existsByProgressivoDelComune(payload.progressivoDelComune())) {
                throw new BadRequestException("Progressivo " + payload.progressivoDelComune()
                        + " già assegnato ad altro comune");
            }
        }

        Provincia provincia = provinciaRepository.findById(payload.provinciaId())
                .orElseThrow(() ->
                        new NotFoundException("Provincia con id " + payload.provinciaId() + " non trovata"));

        found.setProgressivoDelComune(payload.progressivoDelComune());
        found.setNomeComune(payload.nomeComune());
        found.setProvincia(provincia);

        Comune updated = comuneRepository.save(found);
        log.info("Comune aggiornato: ID={}, Nome={}", updated.getId(), updated.getNomeComune());

        return convertToResponseDTO(updated);
    }

    public void findByIdAndDelete(long comuneId) {

        log.debug("Richiesta eliminazione comune ID: {}", comuneId);

        Comune found = comuneRepository.findById(comuneId)
                .orElseThrow(() -> {
                    log.error("Comune con ID {} non trovato", comuneId);
                    return new NotFoundException("Comune con id " + comuneId + " non trovato");
                });

        // Verifica integrità referenziale: controlla se ci sono indirizzi associati
        long indirizziCount = indirizziRepository.countByComuneId(comuneId);
        if (indirizziCount > 0) {
            log.error("Impossibile eliminare comune ID {} ({}): ha {} indirizzi associati",
                    comuneId, found.getNomeComune(), indirizziCount);
            throw new ConflictException(
                    String.format("Impossibile eliminare il comune '%s': " +
                                    "ci sono %d indirizzi associati. Eliminare prima gli indirizzi.",
                            found.getNomeComune(), indirizziCount)
            );
        }

        comuneRepository.delete(found);
        log.info("Comune eliminato: ID={}, Nome={}", comuneId, found.getNomeComune());
    }

    /**
     * Converte l'entità Comune in DTO di risposta completo
     * Include i dati della provincia embedded
     */
    public ComuneResponseDTO convertToResponseDTO(Comune comune) {
        if (comune == null) {
            log.error("Tentativo di conversione di comune null in DTO");
            throw new IllegalStateException("Comune null non può essere convertito in DTO");
        }

        Provincia provincia = comune.getProvincia();

        if (provincia == null) {
            log.error("Comune ID {} ha provincia null - dato corrotto!", comune.getId());
            throw new IllegalStateException(
                    "Comune ID " + comune.getId() + " ha provincia null - integrità dati compromessa"
            );
        }

        ComuneResponseDTO.ProvinciaEmbeddedDTO provinciaDTO =
                new ComuneResponseDTO.ProvinciaEmbeddedDTO(
                        provincia.getId(),
                        provincia.getSigla(),
                        provincia.getProvincia(),
                        provincia.getRegione()
                );

        return new ComuneResponseDTO(
                comune.getId(),
                comune.getProgressivoDelComune(),
                comune.getNomeComune(),
                provinciaDTO
        );
    }

    //metodo per filtrare data una provincia
    public Page<Comune> getListByProvincia(Long idProvincia, Pageable pageable) {

        Page<Comune> listaComuni = comuneRepository.findByProvincia_Id(idProvincia, pageable);

        return listaComuni;
    }
}