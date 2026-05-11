package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.scebackend.model.orc.entities.Usuario;
import pe.gob.onpe.scebackend.model.orc.projections.ReporteTransaccionesRealizadasProjection;
import java.time.LocalDateTime;
import java.util.List;

public interface ReporteTransaccionesRealizadasRepository extends JpaRepository<Usuario, Long> {

    @Query(
            value = """
            SELECT DISTINCT c_usuario
            FROM fn_reporte_transacciones_realizadas(:pi_esquema, null, null, null, null, null)
            """,
            nativeQuery = true
    )
    List<String> listarUsuarios(@Param("pi_esquema") String esquema);

    @Query(
            value = """
            SELECT
                c_centro_computo AS centroComputo,
                c_nombre_centro_computo AS nombreCentroComputo,
                d_fecha_movimiento AS fechaMovimiento,
                c_usuario AS usuario,
                c_apellidos_nombres AS apellidosNombresUsuario,                
                c_movimiento AS movimiento
            FROM fn_reporte_transacciones_realizadas(
                :pi_esquema,
                :pi_centro_computo,
                :pi_c_usuario,
                :pi_fecha_inicial,
                :pi_fecha_final,
                :pi_n_autorizacion
            )
            ORDER BY d_fecha_movimiento DESC
            """,
            nativeQuery = true
    )
    List<ReporteTransaccionesRealizadasProjection> listarReporteTransaccionesRealizadas(
            @Param("pi_esquema") String esquema,
            @Param("pi_centro_computo") Integer centroComputo,
            @Param("pi_c_usuario") String usuario,
            @Param("pi_fecha_inicial") LocalDateTime fechaInicial,
            @Param("pi_fecha_final") LocalDateTime fechaFinal,
            @Param("pi_n_autorizacion") Integer autorizacion
    );





}
