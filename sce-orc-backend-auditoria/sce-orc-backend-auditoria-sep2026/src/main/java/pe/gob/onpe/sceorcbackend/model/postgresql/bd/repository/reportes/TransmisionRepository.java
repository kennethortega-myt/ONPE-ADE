package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import lombok.extern.log4j.Log4j2;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteTransmisionRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ReporteTransmisionResponseDto;

@Log4j2
@Repository
public class TransmisionRepository implements ITransmisionRepository{

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public TransmisionRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	public List<ReporteTransmisionResponseDto> listarTransmision(ReporteTransmisionRequestDto filtro) throws SQLException {
		String sql = ("SELECT * FROM [esquema].fn_reporte_transmision("
				+ ":pi_esquema, "
				+ ":pi_c_centro_computo)")
				.replace("[esquema]", filtro.getEsquema());
		return namedParameterJdbcTemplate.query(sql, mapaerParametrosQuery(filtro), this::llenarDatos);
	}

	private SqlParameterSource mapaerParametrosQuery(ReporteTransmisionRequestDto filtro) throws SQLException {
		return new MapSqlParameterSource("pi_esquema", filtro.getEsquema())
				.addValue("pi_c_centro_computo", filtro.getCodigoCentroComputo());
	}

	private ReporteTransmisionResponseDto llenarDatos(ResultSet resultSet, int i) throws SQLException {
		return ReporteTransmisionResponseDto
				.builder()

				.pc(resultSet.getInt("n_puesta_cero"))
				.descCompu(resultSet.getString("c_centro_computo"))
				.total(resultSet.getInt("n_cant_acta"))
				.re(resultSet.getInt("n_cant_re"))
				.ia(resultSet.getInt("n_cant_ia"))
				.ir(resultSet.getInt("n_canr_ir"))
				.fe(resultSet.getString("d_fecha_ultima_transmision_datos"))
				.rele(resultSet.getInt("n_cant_roele"))
				.rha(resultSet.getInt("n_cant_romm"))
				.rper(resultSet.getInt("n_cant_rper"))
				.rme(resultSet.getInt("n_cant_rmme"))

				.transPresidente(resultSet.getInt("n_trans_presidente"))
				.transParlamentoAndino(resultSet.getInt("n_trans_parlamento_andino"))
				.transDiputados(resultSet.getInt("n_trans_diputados"))
				.transSenDistMultiple(resultSet.getInt("n_trans_senadores_distrito_multiple"))
				.transSenDistUnico(resultSet.getInt("n_trans_senadores_distrito_unico"))

				.pendPresidente(resultSet.getInt("n_cant_por_presidente"))
				.pendParlamentoAndino(resultSet.getInt("n_cant_por_parlamento_andino"))
				.pendDiputados(resultSet.getInt("n_cant_por_diputados"))
				.pendSenDistMultiple(resultSet.getInt("n_cant_por_senadores_distrito_multiple"))
				.pendSenDistUnico(resultSet.getInt("n_cant_por_senadores_distrito_unico"))


				.pr(null)
				.cn(null)
				.pa(null)
				.co(null)
				.rg(null)
				.pv(null)
				.totalDist(null)
				.totalProv(null)
				.totalCons(null)
				.totalReg(null)
				.build();
	}
}