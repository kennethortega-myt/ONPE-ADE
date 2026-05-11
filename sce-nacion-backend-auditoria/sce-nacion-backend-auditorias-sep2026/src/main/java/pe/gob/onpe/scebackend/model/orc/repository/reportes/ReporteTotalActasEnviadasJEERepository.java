package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteTotalEnviadaJEERequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteTotalActasEnviadasJEEDto;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Log4j2
@Repository
public class ReporteTotalActasEnviadasJEERepository implements IReporteTotalActasEnviadasJEERepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ReporteTotalActasEnviadasJEERepository(@Qualifier("namedParameterJdbcTemplateNacion") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<ReporteTotalActasEnviadasJEEDto> listarReporteTotalActasEnviadasJEE(ReporteTotalEnviadaJEERequestDto filtro) throws SQLException {
        String sql = ("SELECT * FROM fn_reporte_actas_enviadas_jee_x_centro_computo(:pi_esquema, " +
                ":pi_eleccion, " +
                ":pi_codigo_ambito, " +
                ":pi_centro_computo, " +
                ":pi_tipo_consulta, " +
                ":pi_agrupado_por)");
        return namedParameterJdbcTemplate.query(sql, mapearParametrosQuery(filtro), (resultSet, i) ->  llenarDatos(resultSet));
    }

    private SqlParameterSource mapearParametrosQuery(ReporteTotalEnviadaJEERequestDto filtro) {

        return new MapSqlParameterSource("pi_esquema",filtro.getEsquema())
                .addValue("pi_eleccion",filtro.getIdEleccion())
                .addValue("pi_codigo_ambito",filtro.getCodigoAmbitoElectoral())
                .addValue("pi_centro_computo",filtro.getCodigoCentroComputo())
                .addValue("pi_tipo_consulta",filtro.getTipoConsulta())
                .addValue("pi_agrupado_por",filtro.getTipoAgrupado());
    }

    private ReporteTotalActasEnviadasJEEDto llenarDatos(ResultSet rs) throws SQLException {
        ReporteTotalActasEnviadasJEEDto reporte = new ReporteTotalActasEnviadasJEEDto();
        reporte.setIdEleccion(rs.getInt("n_eleccion"));
        reporte.setCodigoAmbitoElectoral(rs.getString("c_codigo_ambito"));
        reporte.setCodigoCentroComputo(rs.getString("c_codigo_cc"));
        reporte.setNombreCentroComputo(rs.getString("c_nombre_cc_lc"));
        reporte.setTotalActasPorCentroComputo(rs.getLong("n_actas"));
        reporte.setActa01( rs.getLong("n_actaerrormaterial") + rs.getLong("n_errormaterialagrupacionpolitica") );
        reporte.setActa02(rs.getLong("n_actaimpuganda"));
        reporte.setActa03( rs.getLong("n_ilegiblecva") + rs.getLong("n_ilegibleagrupacionpolitica") );
        reporte.setActa04(rs.getLong("n_actaimcompleta"));
        reporte.setActa05(rs.getLong("n_actanulidad"));
        reporte.setActa06(rs.getLong("n_actasindato"));
        reporte.setActa07(rs.getLong("n_actaextraviada"));
        reporte.setActa08(rs.getLong("n_actasinistrada"));
        reporte.setActa09(rs.getLong("n_actasinfirmaryohuella"));
        reporte.setActa10(rs.getLong("n_masdeunaobservacion") + rs.getLong("n_actareprocesada"));
        reporte.setActa11(rs.getLong("n_actaobservada"));
        reporte.setTotalActasEnvioJEE(rs.getLong("n_total_envio_jee"));
        
        BigDecimal porcentajeJee = rs.getBigDecimal("n_porcentaje_total_envio_jee");      
        reporte.setPorcentajeActasEnvioJEE(porcentajeJee  == null ? 0 : porcentajeJee.doubleValue());
        
        return reporte;
    }
}
