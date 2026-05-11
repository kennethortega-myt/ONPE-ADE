package pe.gob.onpe.sceorcbackend.model.postgresql.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DetTipoEleccionDocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;

import java.util.List;


public interface DetTipoEleccionDocumentoElectoralRepository extends JpaRepository<DetTipoEleccionDocumentoElectoral, Integer> {

  DetTipoEleccionDocumentoElectoral findByRangoInicialLessThanEqualAndRangoFinalGreaterThanEqualAndDocumentoElectoral(String rangoInicial, String rangoFinal, DocumentoElectoral documentoElectoral);

  DetTipoEleccionDocumentoElectoral findByEleccionAndDocumentoElectoral(Eleccion eleccion, DocumentoElectoral documentoElectoral);

  List<DetTipoEleccionDocumentoElectoral> findByDocumentoElectoral(DocumentoElectoral documentoElectoral);

  @Query("""
          SELECT ad
          FROM DetTipoEleccionDocumentoElectoral ad
          JOIN ad.documentoElectoral de
          WHERE de.abreviatura IN (:abreviaturas)
            AND ad.procesoElectoral.id = :idProcesoElectoral and ad.eleccion.id = :idEleccion
                  AND ad.rangoInicial is not null AND ad.rangoInicial <> ''
                        AND ad.digitoChequeo is not null AND ad.digitoChequeo <> '' and ad.activo =1 and de.activo = 1 order by de.abreviatura
      """)
  List<DetTipoEleccionDocumentoElectoral> findConfiguracionDocumentoElectoral(
      @Param("idProcesoElectoral") Long idProcesoElectoral,
      @Param("idEleccion") Long idEleccion,
      @Param("abreviaturas") List<String> abreviaturas
  );


  @Modifying
  @Query("DELETE FROM DetTipoEleccionDocumentoElectoral")
  void deleteAllInBatch();


  @Query("""
          SELECT ad
          FROM DetTipoEleccionDocumentoElectoral ad
          JOIN ad.documentoElectoral de
          WHERE de.abreviatura IN :abreviaturas
            AND :copia BETWEEN ad.rangoInicial AND ad.rangoFinal AND ad.digitoChequeo is not null AND ad.digitoChequeo <> ''
      """)
  List<DetTipoEleccionDocumentoElectoral> findByAbreviaturasAndCopia(
      @Param("abreviaturas") List<String> abreviaturas,
      @Param("copia") String copia
  );

}
