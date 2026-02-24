package it.epicode.bw2.epicenergyservices.controllers;

import it.epicode.bw2.epicenergyservices.dto.response.ComuneDTO;
import it.epicode.bw2.epicenergyservices.services.ComuniService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comuni")
@RequiredArgsConstructor
public class ComuniController {

    private final ComuniService comuniService;

    @GetMapping
    public Page<ComuneDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nomeComune") String orderBy
    ) {
        return comuniService.findAll(page, size, orderBy);
    }

    @GetMapping("/{id}")
    public ComuneDTO getById(@PathVariable long id) {
        return comuniService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComuneDTO create(@RequestBody @Valid ComuneDTO payload) {
        return comuniService.saveComune(payload);
    }

    @PutMapping("/{id}")
    public ComuneDTO update(@PathVariable long id,
                            @RequestBody @Valid ComuneDTO payload) {
        return comuniService.findByIdAndUpdate(id, payload);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        comuniService.findByIdAndDelete(id);
    }
}