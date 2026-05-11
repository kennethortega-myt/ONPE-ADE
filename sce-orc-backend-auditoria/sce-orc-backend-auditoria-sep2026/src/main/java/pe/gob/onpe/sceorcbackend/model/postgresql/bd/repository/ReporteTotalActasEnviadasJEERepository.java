package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import lombok.extern.log4j.Log4j2;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteTotalEnviadaJEERequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ReporteTotalActasEnviadasJEEDto;

@Log4j2
@Repository
public class ReporteTotalActasEnviadasJEERepository implements IReporteTotalActasEnviadasJEERepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ReporteTotalActasEnviadasJEERepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    	this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

	@Override
	public List<ReporteTotalActasEnviadasJEEDto> listarReporteTotalActasEnviadasJEE(
			ReporteTotalEnviadaJEERequestDto filtro) throws SQLException {
        String sql = ("SELECT * FROM [esquema].fn_reporte_actas_enviadas_jee_x_centro_computo(:pi_esquema, " +
                ":pi_eleccion, " +
                ":pi_codigo_ambito, " +
                ":pi_centro_computo, " +
                ":pi_tipo_consulta, " +
                ":pi_agrupado_por)")
                .replace("[esquema]",filtro.getEsquema());
        return namedParameterJdbcTemplate.query(sql, mapearParametrosQuery(filtro), (resultSet, i) ->  llenarDatos(resultSet));
	}

    private SqlParameterSource mapearParametrosQuery(ReporteTotalEnviadaJEERequestDto filtro) throws SQLException {
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
        reporte.setTotalActasPorCentroComputo(rs.getShort("n_actas"));
        reporte.setActa01( (short) (rs.getShort("n_actaerrormaterial") + rs.getShort("n_errormaterialagrupacionpolitica")) );
        reporte.setActa02(rs.getShort("n_actaimpuganda"));
        reporte.setActa03( (short) (rs.getShort("n_ilegiblecva") + rs.getShort("n_ilegibleagrupacionpolitica")) );
        reporte.setActa04(rs.getShort("n_actaimcompleta"));
        reporte.setActa05(rs.getShort("n_actanulidad"));
        reporte.setActa06(rs.getShort("n_actasindato"));
        reporte.setActa07(rs.getShort("n_actaextraviada"));
        reporte.setActa08(rs.getShort("n_actasinistrada"));
        reporte.setActa09(rs.getShort("n_actasinfirmaryohuella"));
        reporte.setActa10((short) (rs.getShort("n_masdeunaobservacion") + rs.getShort("n_actareprocesada")) );
        reporte.setActa11(rs.getShort("n_actaobservada"));
        reporte.setTotalActasEnvioJEE(rs.getShort("n_total_envio_jee"));
        
        Double porcentajeJee = rs.getDouble("n_porcentaje_total_envio_jee");      
        reporte.setPorcentajeActasEnvioJEE(porcentajeJee  == null ? 0 : porcentajeJee);
        
        return reporte;
    }	
}
