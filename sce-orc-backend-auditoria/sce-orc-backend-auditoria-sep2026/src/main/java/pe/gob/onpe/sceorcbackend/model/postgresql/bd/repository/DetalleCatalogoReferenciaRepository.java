package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OrcDetalleCatalogoReferencia;

public interface DetalleCatalogoReferenciaRepository extends JpaRepository<OrcDetalleCatalogoReferencia, Integer> {

}
