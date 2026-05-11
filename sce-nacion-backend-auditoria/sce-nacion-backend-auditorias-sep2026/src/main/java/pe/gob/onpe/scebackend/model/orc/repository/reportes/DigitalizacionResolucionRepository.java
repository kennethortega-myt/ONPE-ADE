package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.scebackend.model.orc.entities.Usuario;
import pe.gob.onpe.scebackend.model.orc.projections.reporte.DigitalizacionResolucionProjection;

public interface DigitalizacionResolucionRepository extends JpaRepository<Usuario, Long> {

	@Query(
			value = """
            SELECT
                c_descripcion_ambito AS codiDesOdpe,
                c_descripcion_centro_computo AS codiDesCompu,
                c_estado_digitalizacion AS estadoDigital,
                c_numero_resolucion AS numeResolucionJNE,
                c_codigo_centro_computo AS codCCompu,
                c_codigo_ambito AS codODPE,
                c_codigo_ubigeo AS codigoUbigeo
            FROM fn_reporte_avance_digitalizacion_resolucion(
                :pi_esquema,
                :pi_n_eleccion,
                :pi_n_centro_computo,
                :pi_c_ubigeo
            )
            """,
			nativeQuery = true
	)
	List<DigitalizacionResolucionProjection> listarReporteAvanceDigitalizacionResolucion(
			@Param("pi_esquema") String esquema,
			@Param("pi_n_eleccion") Integer idEleccion,
			@Param("pi_n_centro_computo") Integer idCentroComputo,
			@Param("pi_c_ubigeo") String ubigeo
	);

}
