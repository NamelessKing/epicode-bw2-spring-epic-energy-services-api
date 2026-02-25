package it.epicode.bw2.epicenergyservices.services;

import it.epicode.bw2.epicenergyservices.dto.request.IndirizzoRequestDTO;
import it.epicode.bw2.epicenergyservices.dto.response.IndirizzoResponseDTO;
import it.epicode.bw2.epicenergyservices.entities.Comune;
import it.epicode.bw2.epicenergyservices.entities.Indirizzo;
import it.epicode.bw2.epicenergyservices.exceptions.BadRequestException;
import it.epicode.bw2.epicenergyservices.exceptions.NotFoundException;
import it.epicode.bw2.epicenergyservices.repositories.ComuneRepository;
import it.epicode.bw2.epicenergyservices.repositories.IndirizziRepository;
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
public class IndirizziService {

    private static final List<String> VALID_SORT_FIELDS =
            Arrays.asList("id", "via", "localita", "cap");
    private final IndirizziRepository indirizziRepository;
    private final ComuneRepository comuneRepository;

    public Page<IndirizzoResponseDTO> findAll(int page, int size, String orderBy) {

        if (size > 200 || size <= 0) size = 10;
        if (page < 0) page = 0;
        if (!VALID_SORT_FIELDS.contains(orderBy)) orderBy = "via";

        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));

        return indirizziRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    public IndirizzoResponseDTO save(IndirizzoRequestDTO payload) {

        Comune comune = comuneRepository.findById(payload.comuneId())
                .orElseThrow(() ->
                        new NotFoundException("Comune con id " + payload.comuneId() + " non trovato"));

        if (indirizziRepository.existsByViaAndCivicoAndLocalitaAndCapAndComune_Id(
                payload.via(), payload.civico(), payload.localita(), payload.cap(), comune.getId())) {

            throw new BadRequestException("Indirizzo già esistente nello stesso comune");
        }

        Indirizzo indirizzo = new Indirizzo(
                payload.via(),
                payload.civico(),
                payload.localita(),
                payload.cap(),
                comune
        );

        Indirizzo saved = indirizziRepository.save(indirizzo);

        log.info("Indirizzo creato ID={}, Via={}", saved.getId(), saved.getVia());

        return convertToResponseDTO(saved);
    }

    public IndirizzoResponseDTO findById(long id) {

        Indirizzo indirizzo = indirizziRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Indirizzo con id " + id + " non trovato"));

        return convertToResponseDTO(indirizzo);
    }

    public IndirizzoResponseDTO findByIdAndUpdate(long id, IndirizzoRequestDTO payload) {

        Indirizzo found = indirizziRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Indirizzo con id " + id + " non trovato"));

        Comune comune = comuneRepository.findById(payload.comuneId())
                .orElseThrow(() ->
                        new NotFoundException("Comune con id " + payload.comuneId() + " non trovato"));

        found.setVia(payload.via());
        found.setCivico(payload.civico());
        found.setLocalita(payload.localita());
        found.setCap(payload.cap());
        found.setComune(comune);

        Indirizzo updated = indirizziRepository.save(found);

        log.info("Indirizzo aggiornato ID={}", updated.getId());

        return convertToResponseDTO(updated);
    }

    public void findByIdAndDelete(long id) {

        Indirizzo found = indirizziRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Indirizzo con id " + id + " non trovato"));

        indirizziRepository.delete(found);

        log.warn("Indirizzo eliminato ID={}", id);
    }

    private IndirizzoResponseDTO convertToResponseDTO(Indirizzo indirizzo) {

        if (indirizzo == null) {
            throw new IllegalStateException("Indirizzo null non convertibile");
        }

        IndirizzoResponseDTO.ComuneEmbeddedDTO comuneDTO =
                new IndirizzoResponseDTO.ComuneEmbeddedDTO(
                        indirizzo.getComune().getId(),
                        indirizzo.getComune().getNomeComune()
                );

        return new IndirizzoResponseDTO(
                indirizzo.getId(),
                indirizzo.getVia(),
                indirizzo.getCivico(),
                indirizzo.getLocalita(),
                indirizzo.getCap(),
                comuneDTO
        );
    }

}
