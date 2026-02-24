package it.epicode.bw2.epicenergyservices.controllers;

import it.epicode.bw2.epicenergyservices.dto.response.ProvinciaDTO;
import it.epicode.bw2.epicenergyservices.services.ProvinceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/province")
@RequiredArgsConstructor
public class ProvinceController {

    private final ProvinceService provinceService;

    @GetMapping
    public Page<ProvinciaDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "provincia") String orderBy
    ) {
        return provinceService.findAll(page, size, orderBy);
    }

    @GetMapping("/{id}")
    public ProvinciaDTO getById(@PathVariable long id) {
        return provinceService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProvinciaDTO create(@RequestBody @Valid ProvinciaDTO payload) {
        return provinceService.saveProvincia(payload);
    }

    @PutMapping("/{id}")
    public ProvinciaDTO update(@PathVariable long id,
                               @RequestBody @Valid ProvinciaDTO payload) {
        return provinceService.findByIdAndUpdate(id, payload);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        provinceService.findByIdAndDelete(id);
    }
}