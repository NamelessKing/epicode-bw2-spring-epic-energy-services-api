package it.epicode.bw2.epicenergyservices.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.epicode.bw2.epicenergyservices.dto.request.ClienteCreateDTO;
import it.epicode.bw2.epicenergyservices.dto.request.ClienteUpdateDTO;
import it.epicode.bw2.epicenergyservices.dto.request.UpdateContattoDTO;
import it.epicode.bw2.epicenergyservices.dto.request.UpdateLogoDTO;
import it.epicode.bw2.epicenergyservices.dto.request.EmailDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ClienteResponseDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ClienteListItemDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ClienteSearchDTO;
import it.epicode.bw2.epicenergyservices.dto.response.ErrorsDTO;
import it.epicode.bw2.epicenergyservices.entities.TipoAzienda;
import it.epicode.bw2.epicenergyservices.services.ClienteService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/clienti")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Clienti", description = "Gestione clienti business - Operazioni CRUD")
public class ClienteController {
    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Crea nuovo cliente", description = "USER/ADMIN: Crea un nuovo cliente business nel sistema con dati aziendali, contatto e indirizzi (sede legale e operativa)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati non validi", content = @Content(schema = @Schema(implementation = ErrorsDTO.class))),
            @ApiResponse(responseCode = "409", description = "Cliente già esistente (partita IVA/email duplicata)", content = @Content(schema = @Schema(implementation = ErrorsDTO.class)))
    })
    public ClienteResponseDTO saveCliente(@RequestBody @Validated ClienteCreateDTO payload) {
        return this.clienteService.save(payload);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
            summary = "Lista clienti con filtri",
            description = "USER/ADMIN: Recupera lista paginata di clienti attivi con filtri dinamici (nome, fatturato, date, provincia, tipo)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista recuperata"),
            @ApiResponse(responseCode = "401", description = "Token non valido")
    })
    public Page<ClienteListItemDTO> getAllActive(
            @Parameter(description = "Numero pagina (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Elementi per pagina (max 100)", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Ordinamento: campo,direzione", example = "ragioneSociale,asc")
            @RequestParam(defaultValue = "ragioneSociale,asc") String sort,
            
            // Filtri opzionali
            @Parameter(description = "Filtra per nome (case-insensitive)", example = "Acme")
            @RequestParam(required = false) String nome,
            
            @Parameter(description = "Fatturato minimo", example = "50000")
            @RequestParam(required = false) Double fatturatoMin,
            
            @Parameter(description = "Fatturato massimo", example = "200000")
            @RequestParam(required = false) Double fatturatoMax,
            
            @Parameter(description = "Data inserimento da (yyyy-MM-dd)", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInserimentoDa,
            
            @Parameter(description = "Data inserimento a (yyyy-MM-dd)", example = "2024-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInserimentoA,
            
            @Parameter(description = "Data ultimo contatto da (yyyy-MM-dd)", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataContattoDa,
            
            @Parameter(description = "Data ultimo contatto a (yyyy-MM-dd)", example = "2024-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataContattoA,
            
            @Parameter(description = "ID provincia sede legale", example = "15")
            @RequestParam(required = false) Long provinciaId,
            
            @Parameter(description = "Tipo azienda (SPA, SRL, PA, ecc)", example = "SPA")
            @RequestParam(required = false) TipoAzienda tipoAzienda
    ) {
        // Valida i parametri
        int safePage = Math.max(0, page);
        int safeSize = size <= 0 || size > 100 ? 20 : size;
        
        // Parsa il parametro sort (formato: "campo,direzione")
        String[] sortParts = sort.split(",");
        String sortField = sortParts[0].trim();
        String sortDir = sortParts.length > 1 ? sortParts[1].trim() : "asc";
        
        // Crea l'oggetto Pageable
        Pageable pageable = PageRequest.of(
                safePage,
                safeSize,
                "desc".equalsIgnoreCase(sortDir) 
                    ? Sort.by(sortField).descending() 
                    : Sort.by(sortField).ascending()
        );
        
        // Chiama il service con tutti i filtri
        return clienteService.findAllActiveWithFilters(
                pageable,
                nome, fatturatoMin, fatturatoMax,
                dataInserimentoDa, dataInserimentoA,
                dataContattoDa, dataContattoA,
                provinciaId, tipoAzienda
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Dettaglio cliente", description = "USER/ADMIN: Visualizza dati completi di un cliente (ragione sociale, indirizzi, contatti) per editing o consultazione dettagliata")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente trovato"),
            @ApiResponse(responseCode = "404", description = "Cliente non trovato", content = @Content(schema = @Schema(implementation = ErrorsDTO.class)))
    })
    public ClienteResponseDTO getById(@PathVariable Long id) {
        return clienteService.toResponseDTO(clienteService.findClienteById(id));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Ricerca clienti", description = "USER/ADMIN: Ricerca rapida clienti per nome (autocomplete). Usato nei form quando bisogna selezionare un cliente per creare fatture")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Risultati ricerca"),
            @ApiResponse(responseCode = "401", description = "Token non valido")
    })
    public List<ClienteSearchDTO> searchClienti(
            @Parameter(description = "Testo da cercare nel nome cliente", example = "rossi")
            @RequestParam String q) {
        return clienteService.searchClienti(q);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifica cliente", description = "ADMIN: Aggiorna dati completi di un cliente (ragione sociale, fatturato, indirizzi, contatto). Solo per amministratori")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente aggiornato"),
            @ApiResponse(responseCode = "404", description = "Cliente non trovato"),
            @ApiResponse(responseCode = "400", description = "Dati non validi")
    })
    public ClienteResponseDTO updateCliente(
            @PathVariable Long id,
            @RequestBody @Validated ClienteUpdateDTO payload) {
        return clienteService.updateCliente(id, payload);
    }

    @PatchMapping("/{id}/logo")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aggiorna logo", description = "ADMIN: Modifica il logo aziendale del cliente. Usato per aggiornare il branding quando cambia l'immagine aziendale")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logo aggiornato"),
            @ApiResponse(responseCode = "404", description = "Cliente non trovato")
    })
    public ClienteResponseDTO updateLogo(
            @PathVariable Long id,
            @RequestBody @Validated UpdateLogoDTO payload) {
        return clienteService.updateLogo(id, payload);
    }

    @PatchMapping("/{id}/contatto")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aggiorna contatto", description = "ADMIN: Modifica i dati del contatto principale del cliente (nome, email, telefono)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contatto aggiornato"),
            @ApiResponse(responseCode = "404", description = "Cliente non trovato")
    })
    public ClienteResponseDTO updateContatto(
            @PathVariable Long id,
            @RequestBody @Validated UpdateContattoDTO payload) {
        return clienteService.updateContatto(id, payload);
    }

    @PatchMapping("/{id}/ultimo-contatto")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aggiorna data ultimo contatto", description = "ADMIN: Registra il contatto odierno con il cliente. Usato per tracciare le comunicazioni")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Data aggiornata"),
            @ApiResponse(responseCode = "404", description = "Cliente non trovato")
    })
    public ClienteResponseDTO updateUltimoContatto(@PathVariable Long id) {
        return clienteService.updateUltimoContatto(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cancella cliente", description = "ADMIN: Soft delete - cancella logicamente un cliente (rimane nel DB con flag cancellato=true)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente cancellato"),
            @ApiResponse(responseCode = "404", description = "Cliente non trovato")
    })
    public void deleteCliente(@PathVariable Long id) {
        clienteService.deleteCliente(id);
    }

    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Riattiva cliente", description = "ADMIN: Riattiva un cliente che era stato cancellato (set cancellato=false). Usato per recuperare cancellazioni accidentali")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente riattivato"),
            @ApiResponse(responseCode = "404", description = "Cliente non trovato")
    })
    public ClienteResponseDTO restoreCliente(@PathVariable Long id) {
        return clienteService.restoreCliente(id);
    }

    @PostMapping("/{id}/email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Invia email al contatto cliente", description = "ADMIN: Invia email al contatto principale del cliente (via Mailgun)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email inviata con successo"),
            @ApiResponse(responseCode = "404", description = "Cliente non trovato"),
            @ApiResponse(responseCode = "400", description = "Dati email non validi")
    })
    public ResponseEntity<String> sendEmailToCliente(
            @PathVariable Long id,
            @RequestBody @Validated EmailDTO payload) {
        clienteService.sendEmailToCliente(id, payload);
        return ResponseEntity.ok("Email inviata con successo al contatto del cliente");
    }
}