package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.projection.ReporteDetalleAvanceRegistroUbigeoProjection;
import java.util.List;

public interface ReporteDetalleAvanceRegistroUbigeoRepository extends JpaRepository<Usuario, Long> {

	@Query(value = """
        SELECT 
            c_codigo_ambito AS codigoAmbitoElectoral,
            c_codigo_centro_computo AS codigoCentroComputo,
            c_codigo_ubigeo AS codigoUbigeo,
            c_departamento AS departamento,
            c_provincia AS provincia,
            c_distrito AS distrito,
            c_procesada AS procesada,
            c_mesa AS mesa,
            n_total_mesa_procesadas AS totalMesasProcesadas,
            n_total_mesas AS totalMesas,
            (n_total_mesas - n_total_mesa_procesadas) AS totalMesaSinProcesar,
            (CAST(n_total_mesa_procesadas AS DECIMAL) / n_total_mesas) AS porcentajeAvance
        FROM fn_reporte_avance_registro_ubigeo_electores(:esquema, :idAmbito, :idCentroComputo, :ubigeo)        
			""",
			nativeQuery = true)
	List<ReporteDetalleAvanceRegistroUbigeoProjection> listarReporteAvanceRegistroUbigeo(
			@Param("esquema") String esquema,
			@Param("idAmbito") Integer idAmbito,
			@Param("idCentroComputo") Integer idCentroComputo,
			@Param("ubigeo") String ubigeo
	);

}
