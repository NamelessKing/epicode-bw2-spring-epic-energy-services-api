package it.epicode.bw2.epicenergyservices.specifications;

import it.epicode.bw2.epicenergyservices.entities.Cliente;
import it.epicode.bw2.epicenergyservices.entities.TipoAzienda;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Crea i filtri dinamici per cercare clienti.
 * Ogni metodo statico ritorna una Specification che filtra per un criterio.
 * Se il valore è null, il filtro non viene applicato.
 * 
 * Usato dal Service: ClienteSpecification.withFilters(...).
 * Passato al Repository: clienteRepository.findAll(spec, pageable).
 */
public class ClienteSpecification {

    // Filtra per nome (LIKE case-insensitive)
    public static Specification<Cliente> hasRagioneSocialeLike(String nome) {
        return (root, query, criteriaBuilder) -> {
            if (nome == null || nome.isBlank()) {
                return criteriaBuilder.conjunction(); // no filter se null
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("ragioneSociale")),
                    "%" + nome.toLowerCase() + "%"
            );
        };
    }

    // Fatturato >= di questo valore
    public static Specification<Cliente> hasFatturatoMin(Double fatturatoMin) {
        return (root, query, criteriaBuilder) -> {
            if (fatturatoMin == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("fatturatoAnnuale"), fatturatoMin);
        };
    }

    // Fatturato <= di questo valore
    public static Specification<Cliente> hasFatturatoMax(Double fatturatoMax) {
        return (root, query, criteriaBuilder) -> {
            if (fatturatoMax == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("fatturatoAnnuale"), fatturatoMax);
        };
    }

    // Data inserimento >= (da)
    public static Specification<Cliente> hasDataInserimentoDa(LocalDate dataInserimentoDa) {
        return (root, query, criteriaBuilder) -> {
            if (dataInserimentoDa == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("dataInserimento"), dataInserimentoDa);
        };
    }

    // Data inserimento <= (a)
    public static Specification<Cliente> hasDataInserimentoA(LocalDate dataInserimentoA) {
        return (root, query, criteriaBuilder) -> {
            if (dataInserimentoA == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("dataInserimento"), dataInserimentoA);
        };
    }

    // Data ultimo contatto >= (da)
    public static Specification<Cliente> hasDataUltimoContattoDa(LocalDate dataContattoDa) {
        return (root, query, criteriaBuilder) -> {
            if (dataContattoDa == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("dataUltimoContatto"), dataContattoDa);
        };
    }

    // Data ultimo contatto <= (a)
    public static Specification<Cliente> hasDataUltimoContattoA(LocalDate dataContattoA) {
        return (root, query, criteriaBuilder) -> {
            if (dataContattoA == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("dataUltimoContatto"), dataContattoA);
        };
    }

    // Filtra per provincia (con JOIN)
    public static Specification<Cliente> hasProvinciaId(Long provinciaId) {
        return (root, query, criteriaBuilder) -> {
            if (provinciaId == null) {
                return criteriaBuilder.conjunction();
            }
            // JOIN: Cliente -> Indirizzo -> Comune -> Provincia
            Join<Object, Object> sedeLegale = root.join("indirizzoSedeLegale");
            Join<Object, Object> comune = sedeLegale.join("comune");
            Join<Object, Object> provincia = comune.join("provincia");
            return criteriaBuilder.equal(provincia.get("id"), provinciaId);
        };
    }

    // Filtra per tipo azienda
    public static Specification<Cliente> hasTipoAzienda(TipoAzienda tipo) {
        return (root, query, criteriaBuilder) -> {
            if (tipo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("tipo"), tipo);
        };
    }

    // Esclude i clienti cancellati
    public static Specification<Cliente> isNotCancellato() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("cancellato"), false);
    }

    // Combina tutti i filtri con AND
    public static Specification<Cliente> withFilters(
            String nome,
            Double fatturatoMin,
            Double fatturatoMax,
            LocalDate dataInserimentoDa,
            LocalDate dataInserimentoA,
            LocalDate dataContattoDa,
            LocalDate dataContattoA,
            Long provinciaId,
            TipoAzienda tipoAzienda
    ) {
        // Combina i filtri: se un parametro è null, viene ignorato
        return Specification
                .where(isNotCancellato())
                .and(hasRagioneSocialeLike(nome))
                .and(hasFatturatoMin(fatturatoMin))
                .and(hasFatturatoMax(fatturatoMax))
                .and(hasDataInserimentoDa(dataInserimentoDa))
                .and(hasDataInserimentoA(dataInserimentoA))
                .and(hasDataUltimoContattoDa(dataContattoDa))
                .and(hasDataUltimoContattoA(dataContattoA))
                .and(hasProvinciaId(provinciaId))
                .and(hasTipoAzienda(tipoAzienda));
    }
}
