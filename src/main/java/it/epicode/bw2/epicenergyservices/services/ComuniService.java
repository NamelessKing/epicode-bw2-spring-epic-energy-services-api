package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.response.ComuneDTO;
import it.epicode.bw2.epicenergyservices.entities.Comune;
import it.epicode.bw2.epicenergyservices.entities.Provincia;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.ComuneRepository;
import it.epicode.bw2.epicenergyservices.repositories.ProvinciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ComuniService {

    private final ComuneRepository comuneRepository;
    private final ProvinciaRepository provinciaRepository;

    public Page<ComuneDTO> findAll(int page, int size, String orderBy) {

        if (size > 200 || size <= 0) size = 10;
        if (page < 0) page = 0;
        if (orderBy == null || orderBy.isBlank()) orderBy = "nomeComune";

        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));

        return comuneRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public ComuneDTO saveComune(ComuneDTO payload) {

        if (comuneRepository.existsByProgressivoDelComune(payload.progressivoDelComune())) {
            throw new BadRequestException("Esiste già un comune con progressivo: "
                    + payload.progressivoDelComune());
        }

        Provincia provincia = provinciaRepository.findById(payload.provinciaId())
                .orElseThrow(() ->
                        new NotFoundException("Provincia non trovata"));

        Comune comune = new Comune(
                payload.progressivoDelComune(),
                payload.nomeComune().trim(),
                provincia
        );

        Comune saved = comuneRepository.save(comune);

        return convertToDTO(saved);
    }

    public Comune findById(long comuneId) {

        Comune comune = comuneRepository.findById(comuneId)
                .orElseThrow(() ->
                        new NotFoundException("Comune con id " + comuneId + " non trovato"));
        System.out.println("Cerco comune con id: " + comuneId);

        return comune;
    }

    public ComuneDTO findByIdAndUpdate(long comuneId, ComuneDTO payload) {

        Comune found = comuneRepository.findById(comuneId)
                .orElseThrow(() ->
                        new NotFoundException("Comune con id " + comuneId + " non trovato"));

        Provincia provincia = provinciaRepository.findById(payload.provinciaId())
                .orElseThrow(() ->
                        new NotFoundException("Provincia non trovata"));

        found.setProgressivoDelComune(payload.progressivoDelComune());
        found.setNomeComune(payload.nomeComune().trim());
        found.setProvincia(provincia);

        Comune updated = comuneRepository.save(found);

        return convertToDTO(updated);
    }

    public void findByIdAndDelete(long comuneId) {

        Comune found = comuneRepository.findById(comuneId)
                .orElseThrow(() ->
                        new NotFoundException("Comune con id " + comuneId + " non trovato"));

        comuneRepository.delete(found);
    }

    private ComuneDTO convertToDTO(Comune comune) {
        return new ComuneDTO(
                comune.getProgressivoDelComune(),
                comune.getNomeComune(),
                comune.getProvincia().getId()
        );
    }
}