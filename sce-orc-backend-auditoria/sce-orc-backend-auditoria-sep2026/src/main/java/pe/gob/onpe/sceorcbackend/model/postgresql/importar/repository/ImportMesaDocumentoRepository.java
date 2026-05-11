package pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MesaDocumento;

public interface ImportMesaDocumentoRepository extends JpaRepository<MesaDocumento, Long> {


}
