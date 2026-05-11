package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MesaDocumento;

import java.util.List;
import java.util.Optional;

public interface MesaDocumentoRepository extends JpaRepository<MesaDocumento, Long> {

    List<MesaDocumento> findByMesaAndAdmDocumentoElectoral(Mesa mesa, DocumentoElectoral documentoElectoral);


    Optional<MesaDocumento> findByMesa_IdAndAdmDocumentoElectoral_IdAndTipoArchivoAndPagina(
        Long idMesa,
        Integer idDocumentoElectoral,
        String tipoArchivo,
        Integer pagina
    );


    @Query(value = """
    SELECT CONCAT('Pág. ', d.n_pagina) AS pagina_label
    FROM det_mesa_documento_electoral_archivo d
    WHERE d.n_documento_electoral = (
        SELECT t.n_documento_electoral_pk 
        FROM tab_documento_electoral t 
        WHERE t.c_abreviatura = :abreviatura
    )
    AND d.n_mesa = :idMesa 
    AND d.c_tipo_archivo = 'image/tiff'
    ORDER BY d.n_pagina
    """, nativeQuery = true)
    List<String> findPaginasConEtiquetaByMesaAndAbreviatura(@Param("idMesa") Long idMesa, @Param("abreviatura") String abreviatura);

    @Modifying
    @Query("DELETE FROM MesaDocumento")
    void deleteAllInBatch();


    void deleteByAdmDocumentoElectoral_IdAndMesa_Id(Integer admDocumentoElectoralId, Long mesaId);


    @Query("SELECT md FROM MesaDocumento md "
    		+ "JOIN md.mesa m "
    		+ "WHERE m.id = ?1 ")
    List<MesaDocumento> findByMesaId(Long idMesa);

    List<MesaDocumento> findByMesa_IdAndAdmDocumentoElectoral_Id(Long idMesa, Integer IdDocumentoElectoral);


}
