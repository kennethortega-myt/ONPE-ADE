package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.JneTransmisionRecepcion;

public interface JneTransmisionRecepcionRepository extends JpaRepository<JneTransmisionRecepcion, Long> {
    boolean existsByCodigoJneEnvio(String codigoJneEnvio);
}
