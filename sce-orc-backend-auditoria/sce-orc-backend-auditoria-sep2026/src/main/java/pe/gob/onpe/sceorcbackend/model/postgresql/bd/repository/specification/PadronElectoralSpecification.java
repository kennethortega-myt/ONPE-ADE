package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import pe.gob.onpe.sceorcbackend.model.dto.PadronElectoralBusquedaDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.PadronElectoral;

import java.util.ArrayList;
import java.util.List;

public class PadronElectoralSpecification {
    public static Specification<PadronElectoral> buscarPorCriterios(PadronElectoralBusquedaDto criterios) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por DNI (búsqueda exacta o similar)
            if (criterios.getDni() != null && !criterios.getDni().trim().isEmpty()) {
                String dni = criterios.getDni().trim();
                if (dni.length() == 8) {
                    // Búsqueda exacta si tiene 8 dígitos
                    predicates.add(criteriaBuilder.equal(root.get("documentoIdentidad"), dni));
                } else {
                    // Búsqueda similar si tiene menos de 8 dígitos
                    predicates.add(criteriaBuilder.like(root.get("documentoIdentidad"), "%" + dni + "%"));
                }
            }

            // Filtro por número de mesa (búsqueda exacta)
            if (criterios.getNumeroMesa() != null) {
                predicates.add(criteriaBuilder.equal(root.get("mesaId"), criterios.getNumeroMesa()));
            }

            // Filtro por nombres (búsqueda similar - LIKE)
            if (criterios.getNombres() != null && !criterios.getNombres().trim().isEmpty()) {
                String nombres = criterios.getNombres().trim().toUpperCase();
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("nombres")),
                        "%" + nombres + "%"
                ));
            }

            // Filtro por apellido paterno (búsqueda similar - LIKE)
            if (criterios.getApellidoPaterno() != null && !criterios.getApellidoPaterno().trim().isEmpty()) {
                String apellidoPaterno = criterios.getApellidoPaterno().trim().toUpperCase();
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("apellidoPaterno")),
                        "%" + apellidoPaterno + "%"
                ));
            }

            // Filtro por apellido materno (búsqueda similar - LIKE)
            if (criterios.getApellidoMaterno() != null && !criterios.getApellidoMaterno().trim().isEmpty()) {
                String apellidoMaterno = criterios.getApellidoMaterno().trim().toUpperCase();
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("apellidoMaterno")),
                        "%" + apellidoMaterno + "%"
                ));
            }

            // Filtro por activo
            predicates.add(criteriaBuilder.equal(root.get("activo"), 1));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
