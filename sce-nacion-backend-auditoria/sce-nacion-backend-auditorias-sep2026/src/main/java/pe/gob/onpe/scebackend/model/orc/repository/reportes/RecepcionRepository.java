package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import lombok.extern.log4j.Log4j2;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteRecepcionRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteRecepcionResponseDto;

@Log4j2
@Repository
public class RecepcionRepository implements IRecepcionRepository{

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public RecepcionRepository(@Qualifier("namedParameterJdbcTemplateNacion") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
	    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}
	
	public List<ReporteRecepcionResponseDto> listarRecepcion(ReporteRecepcionRequestDto filtro) throws SQLException {
		String sql = ("SELECT * FROM fn_reporte_recepcion("
        		+ ":pi_esquema, "        		
        		+ ":pi_c_centro_computo, "
        		+ ":pi_fecha_inicial, "
        		+ ":pi_fecha_final)");

        return namedParameterJdbcTemplate.query(sql, mapaerParametrosQuery(filtro), this::llenarDatos);
        
	}
	
	private SqlParameterSource mapaerParametrosQuery(ReporteRecepcionRequestDto filtro) {
		Calendar calendarInicial = Calendar.getInstance();
		calendarInicial.setTime(filtro.getFechaInicial());
		calendarInicial.set(Calendar.HOUR_OF_DAY, 0);
		calendarInicial.set(Calendar.MINUTE, 0);
		calendarInicial.set(Calendar.SECOND, 0);
		
		Calendar calendarFinal = Calendar.getInstance();
		calendarFinal.setTime(filtro.getFechaFin());
		calendarFinal.set(Calendar.HOUR_OF_DAY, 23);
		calendarFinal.set(Calendar.MINUTE, 59);
		calendarFinal.set(Calendar.SECOND, 59);

		System.out.println("calendarInicial = " + calendarInicial.getTime());
		System.out.println("calendarFinal = " + calendarFinal.getTime());
		
		System.out.println("calendarInicial getTimeZone = " + calendarInicial.getTimeZone());
		System.out.println("calendarFinal getTimeZone = " + calendarFinal.getTimeZone());
		
        return new MapSqlParameterSource("pi_esquema", filtro.getEsquema())
                .addValue("pi_c_centro_computo", filtro.getCodigoCentroComputo())
                .addValue("pi_fecha_inicial", calendarInicial.getTime())
                .addValue("pi_fecha_final", calendarFinal.getTime());
    }
	
	private ReporteRecepcionResponseDto llenarDatos(ResultSet resultSet, int i) throws SQLException {
		return ReporteRecepcionResponseDto
				.builder()
				.presidencialRecibida(resultSet.getInt("n_recibida_presidente"))
				.parlamentoRecibida(resultSet.getInt("n_recibida_parlamento_andino"))
				.diputadoRecibida(resultSet.getInt("n_recibida_diputados"))
				.multipleRecibida(resultSet.getInt("n_recibida_senadores_distrito_multiple"))
				.unicoRecibida(resultSet.getInt("n_recibida_senadores_distrito_unico"))
				
				.presidencialTotal(resultSet.getInt("n_cant_por_presidente"))
				.parlamentoTotal(resultSet.getInt("n_cant_por_parlamento_andino"))
				.diputadoTotal(resultSet.getInt("n_cant_por_diputados"))
				.multipleTotal(resultSet.getInt("n_cant_por_senadores_distrito_multiple"))
				.unicoTotal(resultSet.getInt("n_cant_por_senadores_distrito_unico"))
				
				
				.fe(resultSet.getString("d_fecha_ultima_recepcion_datos"))
				.pc(resultSet.getInt("n_pc"))
				.descCompu(resultSet.getString("c_nombre_centro_computo"))
				
				.rele(resultSet.getInt("n_cant_rele"))
				.rha(resultSet.getInt("n_cant_rha"))
				.rper(resultSet.getInt("n_cant_per"))
				.rme(resultSet.getInt("n_cant_rme"))
				
				.re(resultSet.getInt("n_cant_re"))
				.ia(resultSet.getInt("n_cant_ia"))
				.ir(resultSet.getInt("n_cant_ir"))
				.build();
	}
}
