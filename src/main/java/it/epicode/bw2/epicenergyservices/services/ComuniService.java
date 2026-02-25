package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.ComuneRequestDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ComuneResponseDTO;
import it.epicode.bw2.epicenergyservices.entities.Comune;
import it.epicode.bw2.epicenergyservices.entities.Provincia;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.ComuneRepository;
import it.epicode.bw2.epicenergyservices.repositories.ProvinciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComuniService {

    private final ComuneRepository comuneRepository;
    private final ProvinciaRepository provinciaRepository;

    public Page<ComuneResponseDTO> findAll(int page, int size, String orderBy) {

        if (size > 200 || size <= 0) size = 10;
        if (page < 0) page = 0;
        if (orderBy == null || orderBy.isBlank()) orderBy = "nomeComune";

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

    public ComuneResponseDTO findById(long comuneId) {

        Comune comune = comuneRepository.findById(comuneId)
                .orElseThrow(() ->
                        new NotFoundException("Comune con id " + comuneId + " non trovato"));

        return convertToResponseDTO(comune);
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

        Comune found = comuneRepository.findById(comuneId)
                .orElseThrow(() ->
                        new NotFoundException("Comune con id " + comuneId + " non trovato"));

        comuneRepository.delete(found);
        log.info("Comune eliminato: ID={}", comuneId);
    }

    /**
     * Converte l'entità Comune in DTO di risposta completo
     * Include i dati della provincia embedded
     */
    private ComuneResponseDTO convertToResponseDTO(Comune comune) {
        Provincia provincia = comune.getProvincia();
        
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
}