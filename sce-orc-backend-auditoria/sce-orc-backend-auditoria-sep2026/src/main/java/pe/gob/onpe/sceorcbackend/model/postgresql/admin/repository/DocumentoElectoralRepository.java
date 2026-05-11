package pe.gob.onpe.sceorcbackend.model.postgresql.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DocumentoElectoral;
import java.util.List;

public interface DocumentoElectoralRepository extends JpaRepository<DocumentoElectoral, Integer> {

    DocumentoElectoral findByAbreviatura(String cAbreviatura);

    List<DocumentoElectoral> findByDocumentoElectoralPadre(DocumentoElectoral documentoElectoralPadre);

    List<DocumentoElectoral> findByDocumentoElectoralPadreIsNullAndActivo(Integer activo);

    @Query("""
    SELECT DISTINCT d
    FROM DetTipoEleccionDocumentoElectoral dt
    JOIN dt.documentoElectoral d
    JOIN dt.procesoElectoral p
    WHERE p.acronimo = :acronimo
    """)
    List<DocumentoElectoral> buscarDocumentosConfigurados(@Param("acronimo") String acronimoProceso);
    
    @Modifying
    @Query("DELETE FROM DocumentoElectoral")
    void deleteAllInBatch();
}
