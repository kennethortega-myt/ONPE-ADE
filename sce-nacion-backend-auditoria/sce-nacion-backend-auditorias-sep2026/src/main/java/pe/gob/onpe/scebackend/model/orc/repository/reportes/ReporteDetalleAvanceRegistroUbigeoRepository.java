package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteDetalleAvanceRegistroUbigeoRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteDetalleAvanceRegistroUbigeoDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Log4j2
@Repository
public class ReporteDetalleAvanceRegistroUbigeoRepository implements IReporteDetalleAvanceRegistroUbigeoRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ReporteDetalleAvanceRegistroUbigeoRepository(@Qualifier("namedParameterJdbcTemplateNacion") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {

        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<ReporteDetalleAvanceRegistroUbigeoDto> listarReporteDetalleAvanceRegistroUbigeoMiembroMesa(ReporteDetalleAvanceRegistroUbigeoRequestDto filtro) throws SQLException {
        String sql ="SELECT " +
                "c_codigo_ambito, " +
                "c_codigo_centro_computo, " +
                "c_codigo_ubigeo, " +
                "c_departamento, " +
                "c_provincia, " +
                "c_distrito, " +
                "c_procesada, " +
                "c_mesa, " +
                "n_total_mesa_procesadas, " +
                "n_total_mesas, " +
                "(n_total_mesas-n_total_mesa_procesadas) AS n_mesa_sin_procesar, " +
                "(cast(n_total_mesa_procesadas AS decimal)/n_total_mesas) AS n_porcentaje_avance " +
                "FROM fn_reporte_avance_registro_ubigeo_miembro_mesa(:pi_esquema, :pi_ambito, :pi_centro_computo, :pi_ubigeo)";

        return namedParameterJdbcTemplate.query(sql, mapearParametrosQuery(filtro), (resultSet, i) -> llenarDatos(resultSet));
    }

    @Override
    public List<ReporteDetalleAvanceRegistroUbigeoDto> listarReporteDetalleAvanceRegistroUbigeoElector(ReporteDetalleAvanceRegistroUbigeoRequestDto filtro) throws SQLException {
        String sql ="SELECT " +
                "c_codigo_ambito, " +
                "c_codigo_centro_computo, " +
                "c_codigo_ubigeo, " +
                "c_departamento, " +
                "c_provincia, " +
                "c_distrito, " +
                "c_procesada, " +
                "c_mesa, " +
                "n_total_mesa_procesadas, " +
                "n_total_mesas, " +
                "(n_total_mesas-n_total_mesa_procesadas) AS n_mesa_sin_procesar, " +
                "(cast(n_total_mesa_procesadas AS decimal)/n_total_mesas) AS n_porcentaje_avance " +
                "FROM fn_reporte_avance_registro_ubigeo_electores(:pi_esquema, :pi_ambito, :pi_centro_computo, :pi_ubigeo)";
        return namedParameterJdbcTemplate.query(sql, mapearParametrosQuery(filtro), (resultSet, i) -> llenarDatos(resultSet));
    }

    private SqlParameterSource mapearParametrosQuery(ReporteDetalleAvanceRegistroUbigeoRequestDto    filtro) {
        return new MapSqlParameterSource("pi_esquema",filtro.getEsquema())
                .addValue("pi_ambito",filtro.getIdAmbitoElectoral())
                .addValue("pi_centro_computo",filtro.getIdCentroComputo())
                .addValue("pi_ubigeo",filtro.getUbigeo())
                ;
    }

    private ReporteDetalleAvanceRegistroUbigeoDto llenarDatos(ResultSet rs) throws SQLException {
        ReporteDetalleAvanceRegistroUbigeoDto reporte = new ReporteDetalleAvanceRegistroUbigeoDto();
        reporte.setCodigoAmbitoElectoral(rs.getString("c_codigo_ambito"));
        reporte.setCodigoCentroComputo(rs.getString("c_codigo_centro_computo"));
        reporte.setCodigoUbigeo(rs.getString("c_codigo_ubigeo"));
        reporte.setDepartamento(rs.getString("c_departamento"));
        reporte.setProvincia(rs.getString("c_provincia"));
        reporte.setDistrito(rs.getString("c_distrito"));
        reporte.setProcesada(rs.getString("c_procesada"));
        reporte.setMesa(rs.getString("c_mesa"));
        reporte.setTotalMesasProcesadas(rs.getInt("n_total_mesa_procesadas"));
        reporte.setTotalMesas(rs.getInt("n_total_mesas"));
        reporte.setTotalMesaSinProcesar(rs.getInt("n_mesa_sin_procesar"));
        reporte.setPorcentajeAvance(rs.getDouble("n_porcentaje_avance"));
        return reporte;
    }
}
