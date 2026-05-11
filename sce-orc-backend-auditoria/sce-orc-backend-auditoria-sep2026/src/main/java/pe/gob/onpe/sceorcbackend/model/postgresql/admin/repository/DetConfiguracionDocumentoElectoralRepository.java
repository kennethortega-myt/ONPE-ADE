package pe.gob.onpe.sceorcbackend.model.postgresql.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DetConfiguracionDocumentoElectoral;


public interface DetConfiguracionDocumentoElectoralRepository extends JpaRepository<DetConfiguracionDocumentoElectoral, Integer> {

  @Modifying
  @Query("DELETE FROM DetConfiguracionDocumentoElectoral")
  void deleteAllInBatch();

}
