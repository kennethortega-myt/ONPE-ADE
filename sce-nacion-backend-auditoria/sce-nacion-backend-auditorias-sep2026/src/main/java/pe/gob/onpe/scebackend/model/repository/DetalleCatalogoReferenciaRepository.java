package pe.gob.onpe.scebackend.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.scebackend.model.entities.DetalleCatalogoReferencia;

import java.util.List;

public interface DetalleCatalogoReferenciaRepository  extends JpaRepository<DetalleCatalogoReferencia, Integer> {

    List<DetalleCatalogoReferencia> findByTablaReferencia(String tabla);

}
