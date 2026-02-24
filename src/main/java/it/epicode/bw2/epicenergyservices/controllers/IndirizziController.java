package it.epicode.bw2.epicenergyservices.controllers;

import it.epicode.bw2.epicenergyservices.dto.response.IndirizziDTO;
import it.epicode.bw2.epicenergyservices.entities.Indirizzo;
import it.epicode.bw2.epicenergyservices.services.IndirizziService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/indirizzo")
public class IndirizziController {
    private final IndirizziService indirizziService;

    @Autowired
    public IndirizziController(IndirizziService indirizziService) {
        this.indirizziService = indirizziService;
    }

    //    GET http://localhost:8080/indirizzo
    @GetMapping
    public List<Indirizzo> findAll() {
        return this.indirizziService.findAll();
    }

    //    POST http://localhost:8080/indirizzo
    @PostMapping
    public Indirizzo save(@RequestBody @Valid IndirizziDTO payload) {
        return indirizziService.save(payload);

    }

    //    GET http://localhost:8080/indirizzo/483920
    @GetMapping("/{id}")
    public Indirizzo findById(@PathVariable long id) {
        return indirizziService.findById(id);
    }

    //    PUT http://localhost:8080/indirizzo/483920 con il json (PAYLOA)
    @PutMapping("/{id}")
    public Indirizzo update(@PathVariable long id,
                            @RequestBody @Valid IndirizziDTO body) {

        return indirizziService.findByIdAndUpdate(id, body);

    }

    //   DELETE http://localhost:8080/indirizzo/483920
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        indirizziService.findByIdAndDelete(id);
    }
}
