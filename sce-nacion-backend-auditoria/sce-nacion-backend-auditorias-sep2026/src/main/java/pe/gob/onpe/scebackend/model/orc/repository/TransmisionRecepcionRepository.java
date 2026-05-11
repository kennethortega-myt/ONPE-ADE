package pe.gob.onpe.scebackend.model.orc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import pe.gob.onpe.scebackend.model.orc.entities.TransmisionRecepcion;

import java.util.List;

public interface TransmisionRecepcionRepository extends JpaRepository<TransmisionRecepcion, Long> {



    @Query("SELECT a FROM TransmisionRecepcion a WHERE a.acta = ?1")
    public List<TransmisionRecepcion> findByRecepcionStae(Long idActa);

}
