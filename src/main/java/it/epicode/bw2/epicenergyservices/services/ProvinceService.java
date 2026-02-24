package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.response.ProvinciaDTO;
import it.epicode.bw2.epicenergyservices.entities.Provincia;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.ProvinciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProvinceService {

    private final ProvinciaRepository provinciaRepository;

    public Page<ProvinciaDTO> findAll(int page, int size, String orderBy) {

        if (size > 200 || size <= 0) size = 10;
        if (page < 0) page = 0;
        if (orderBy == null || orderBy.isBlank()) orderBy = "provincia";

        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));

        return provinciaRepository.findAll(pageable)
                .map(this::convertToDTO);
    }


    public ProvinciaDTO saveProvincia(ProvinciaDTO payload) {

        String sigla = payload.sigla().trim().toUpperCase();

        if (provinciaRepository.existsBySiglaIgnoreCase(sigla)) {
            throw new BadRequestException("Esiste già una provincia con sigla: " + sigla);
        }

        Provincia provincia = new Provincia(
                sigla,
                payload.provincia().trim(),
                payload.regione().trim()
        );

        Provincia saved = provinciaRepository.save(provincia);

        return convertToDTO(saved);
    }

    public ProvinciaDTO findById(long provinciaId) {

        Provincia provincia = provinciaRepository.findById(provinciaId)
                .orElseThrow(() ->
                        new NotFoundException("Provincia con id " + provinciaId + " non trovata"));

        return convertToDTO(provincia);
    }

    public ProvinciaDTO findByIdAndUpdate(long provinciaId, ProvinciaDTO payload) {

        Provincia found = provinciaRepository.findById(provinciaId)
                .orElseThrow(() ->
                        new NotFoundException("Provincia con id " + provinciaId + " non trovata"));

        String newSigla = payload.sigla().trim().toUpperCase();

        if (!found.getSigla().equalsIgnoreCase(newSigla)
                && provinciaRepository.existsBySiglaIgnoreCase(newSigla)) {
            throw new BadRequestException("Esiste già una provincia con sigla: " + newSigla);
        }

        found.setSigla(newSigla);
        found.setProvincia(payload.provincia().trim());
        found.setRegione(payload.regione().trim());

        Provincia updated = provinciaRepository.save(found);

        return convertToDTO(updated);
    }

    public void findByIdAndDelete(long provinciaId) {

        Provincia found = provinciaRepository.findById(provinciaId)
                .orElseThrow(() ->
                        new NotFoundException("Provincia con id " + provinciaId + " non trovata"));

        provinciaRepository.delete(found);
    }

    private ProvinciaDTO convertToDTO(Provincia provincia) {
        return new ProvinciaDTO(
                provincia.getSigla(),
                provincia.getProvincia(),
                provincia.getRegione()
        );
    }
}