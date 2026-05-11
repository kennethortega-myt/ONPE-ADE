package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import lombok.extern.log4j.Log4j2;
import pe.gob.onpe.scebackend.model.dto.request.reporte.AutoridadesRevocadasRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.AutoridadesRevocadasResponseDto;

@Log4j2
@Repository
public class AutoridadesRevocadasRepository implements IAutoridadesRevocadasRepository{

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public AutoridadesRevocadasRepository(@Qualifier("namedParameterJdbcTemplateNacion") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
	
	public List<AutoridadesRevocadasResponseDto> listaAutoridadesRevocadas(AutoridadesRevocadasRequestDto filtro) throws SQLException {
        String sql = ("SELECT * FROM fn_reporte_autoridades_revocadas("
        		+ ":pi_esquema, "
        		+ ":pi_eleccion, "
        		+ ":pi_n_ambito_electoral, "        		
        		+ ":pi_n_cargo)");

        return namedParameterJdbcTemplate.query(sql, mapaerParametrosQuery(filtro), (resultSet, i) ->  llenarDatos(resultSet));
        
    }
	
	private SqlParameterSource mapaerParametrosQuery(AutoridadesRevocadasRequestDto filtro) {

        return new MapSqlParameterSource("pi_esquema", filtro.getEsquema())
                .addValue("pi_eleccion", filtro.getIdEleccion())                
                .addValue("pi_n_ambito_electoral", filtro.getIdAmbitoElectoral())
                .addValue("pi_n_cargo", filtro.getIdCargo());
    }
	
	private AutoridadesRevocadasResponseDto llenarDatos(ResultSet resultSet) throws SQLException{
		return AutoridadesRevocadasResponseDto
				.builder()
				.orden(resultSet.getInt("n_lista"))
				.departamento(resultSet.getString("c_departamento"))
				.provincia(resultSet.getString("c_provincia"))
				.distrito(resultSet.getString("c_distrito"))
				.descCargo(resultSet.getString("c_cargo"))
				.nombre(resultSet.getString("c_apellidos_nombres"))
				.votosSI(resultSet.getInt("c_voto_si"))
				.votosNO(resultSet.getInt("c_voto_no"))
				.elecHabil(resultSet.getInt("n_electoreshabiles"))
				.totCiudadVotaron(resultSet.getInt("n_total_ciudadanos_votaron"))
				.build();
	}
}
