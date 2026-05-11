package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.onpe.scebackend.model.orc.entities.MesaDocumento;


public interface MesaDocumentoRepository extends JpaRepository<MesaDocumento, Long> {

    Optional<MesaDocumento> findByIdCc(String idCc);
    
}
