package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.ProvinciaRequestDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ProvinciaResponseDTO;
import it.epicode.bw2.epicenergyservices.entities.Provincia;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.ConflictException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.ComuneRepository;
import it.epicode.bw2.epicenergyservices.repositories.ProvinciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProvinceService {

    private final ProvinciaRepository provinciaRepository;
    private final ComuneRepository comuneRepository;
    
    // Campi validi per ordinamento
    private static final List<String> VALID_SORT_FIELDS = Arrays.asList(
        "id", "sigla", "provincia", "regione"
    );

    public Page<ProvinciaResponseDTO> findAll(int page, int size, String orderBy) {

        // Validazione size
        if (size > 200 || size <= 0) {
            log.warn("Size non valido ({}), impostato a default: 10", size);
            size = 10;
        }
        
        // Validazione page
        if (page < 0) {
            log.warn("Page non valido ({}), impostato a default: 0", page);
            page = 0;
        }
        
        // Validazione orderBy - previene SQL injection e errori
        if (orderBy == null || orderBy.isBlank() || !VALID_SORT_FIELDS.contains(orderBy)) {
            if (orderBy != null && !orderBy.isBlank()) {
                log.warn("Campo ordinamento '{}' non valido. Campi validi: {}. Uso default 'provincia'", 
                         orderBy, VALID_SORT_FIELDS);
            }
            orderBy = "provincia";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));

        return provinciaRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }


    public ProvinciaResponseDTO saveProvincia(ProvinciaRequestDTO payload) {

        log.debug("Tentativo di creazione provincia: {} ({})", payload.provincia(), payload.sigla());

        String sigla = payload.sigla(); // già normalizzata dal compact constructor

        if (provinciaRepository.existsBySiglaIgnoreCase(sigla)) {
            log.error("Provincia con sigla {} già esistente", sigla);
            throw new BadRequestException("Esiste già una provincia con sigla: " + sigla);
        }

        Provincia provincia = new Provincia(
                sigla,
                payload.provincia(),
                payload.regione()
        );

        Provincia saved = provinciaRepository.save(provincia);
        log.info("Provincia creata: ID={}, Sigla={}, Nome={}", saved.getId(), saved.getSigla(), saved.getProvincia());

        return convertToResponseDTO(saved);
    }

    public ProvinciaResponseDTO findById(long provinciaId) {

        log.debug("Ricerca provincia con ID: {}", provinciaId);

        Provincia provincia = provinciaRepository.findById(provinciaId)
                .orElseThrow(() -> {
                    log.error("Provincia con ID {} non trovata", provinciaId);
                    return new NotFoundException("Provincia con id " + provinciaId + " non trovata");
                });

        return convertToResponseDTO(provincia);
    }

    public ProvinciaResponseDTO findByIdAndUpdate(long provinciaId, ProvinciaRequestDTO payload) {

        log.debug("Aggiornamento provincia ID: {} con dati: {}", provinciaId, payload);

        Provincia found = provinciaRepository.findById(provinciaId)
                .orElseThrow(() -> {
                    log.error("Provincia con ID {} non trovata", provinciaId);
                    return new NotFoundException("Provincia con id " + provinciaId + " non trovata");
                });

        String newSigla = payload.sigla(); // già normalizzata dal compact constructor

        // Verifica se la sigla cambia e se è già in uso
        if (!found.getSigla().equalsIgnoreCase(newSigla)
                && provinciaRepository.existsBySiglaIgnoreCase(newSigla)) {
            log.error("Sigla {} già in uso da altra provincia", newSigla);
            throw new BadRequestException("Esiste già una provincia con sigla: " + newSigla);
        }

        found.setSigla(newSigla);
        found.setProvincia(payload.provincia());
        found.setRegione(payload.regione());

        Provincia updated = provinciaRepository.save(found);
        log.info("Provincia aggiornata: ID={}, Sigla={}", updated.getId(), updated.getSigla());

        return convertToResponseDTO(updated);
    }

    public void findByIdAndDelete(long provinciaId) {

        log.debug("Richiesta eliminazione provincia ID: {}", provinciaId);

        Provincia found = provinciaRepository.findById(provinciaId)
                .orElseThrow(() -> {
                    log.error("Provincia con ID {} non trovata", provinciaId);
                    return new NotFoundException("Provincia con id " + provinciaId + " non trovata");
                });

        // Verifica integrità referenziale: controlla se ci sono comuni associati
        long comuniCount = comuneRepository.countByProvinciaId(provinciaId);
        if (comuniCount > 0) {
            log.error("Impossibile eliminare provincia ID {} ({}): ha {} comuni associati", 
                      provinciaId, found.getSigla(), comuniCount);
            throw new ConflictException(
                String.format("Impossibile eliminare la provincia '%s' (%s): " +
                             "ci sono %d comuni associati. Eliminare prima i comuni.",
                             found.getProvincia(), found.getSigla(), comuniCount)
            );
        }

        provinciaRepository.delete(found);
        log.info("Provincia eliminata: ID={}, Sigla={}", provinciaId, found.getSigla());
    }

    /**
     * Converte l'entità Provincia in DTO di risposta completo
     */
    private ProvinciaResponseDTO convertToResponseDTO(Provincia provincia) {
        if (provincia == null) {
            log.error("Tentativo di conversione di provincia null in DTO");
            throw new IllegalStateException("Provincia null non può essere convertita in DTO");
        }
        
        return new ProvinciaResponseDTO(
                provincia.getId(),
                provincia.getSigla(),
                provincia.getProvincia(),
                provincia.getRegione()
        );
    }
}