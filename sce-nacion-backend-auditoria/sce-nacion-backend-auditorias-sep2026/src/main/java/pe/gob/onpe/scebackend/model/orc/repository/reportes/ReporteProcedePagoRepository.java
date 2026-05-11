package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.scebackend.model.orc.entities.Usuario;
import pe.gob.onpe.scebackend.model.orc.projections.reporte.ReporteProcedePagoProjection;
import java.util.List;

public interface ReporteProcedePagoRepository extends JpaRepository<Usuario, Long> {


    @Query(value = """
        SELECT 
            nro AS nro,
            c_mesa AS numeroMesa,
            c_documento_identidad AS numeroDocumento,
            c_apellido_nombre AS votante,
            c_cargo AS cargo,
            c_procede AS procedePago
        FROM fn_reporte_procede_pago(:esquema, :tipoReporte)
        """, nativeQuery = true)
    List<ReporteProcedePagoProjection> listarReporteProcedePago(
            @Param("esquema") String esquema,
            @Param("tipoReporte") String tipoReporte
    );
}
