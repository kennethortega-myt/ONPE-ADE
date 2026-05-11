package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.scebackend.model.orc.entities.Usuario;
import pe.gob.onpe.scebackend.model.orc.projections.reporte.ReporteMesasSinOmisosProjection;
import java.util.List;

public interface ReporteMesasSinOmisosElectoresRepository extends JpaRepository<Usuario, Long> {

    @Query(value = """
        SELECT 
            c_codigo_ubigeo AS codigoUbigeo,
            c_codigo_odpe AS codigoODPE,
            c_codigo_centro_computo AS codigoCentroComputo,
            c_departamento AS departamento,
            c_provincia AS provincia,
            c_distrito AS distrito,
            c_mesa AS numeroMesa,
            n_mesas AS totalMesas
        FROM fn_reporte_lista_mesa_sin_omisos_electores(:pi_esquema, :pi_id_eleccion, :pi_id_centro_computo)
    """, nativeQuery = true)
    List<ReporteMesasSinOmisosProjection> listarReporteMesasSinOmisos(
            @Param("pi_esquema") String esquema,
            @Param("pi_id_eleccion") Integer idEleccion,
            @Param("pi_id_centro_computo") Integer idCentroComputo
    );
}