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
import pe.gob.onpe.scebackend.model.dto.request.reporte.AsistenciaMiembroMesaRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.AsistenciaMiembroMesaResponseDto;

@Log4j2
@Repository
public class AsistenciaMiembrosMesaRepository implements IAsistenciaMiembrosMesaRepository{

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public AsistenciaMiembrosMesaRepository(@Qualifier("namedParameterJdbcTemplateNacion") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
	
	@Override
	public List<AsistenciaMiembroMesaResponseDto> listaAsistenciaMM(AsistenciaMiembroMesaRequestDto filtro) throws SQLException{
		String sql = ("SELECT * FROM fn_reporte_lista_asistencia_miembros_mesa("
	    		+ ":pi_esquema, "
	    		+ ":pi_eleccion, "
	    		+ ":pi_n_centro_computo, " 
	    		+ ":pi_c_ubigeo, "
	    		+ ":pi_n_mesa)");
	
	    return namedParameterJdbcTemplate.query(sql, mapaerParametrosQuery(filtro), (resultSet, i) ->  llenarDatos(resultSet));
    
	}

	private SqlParameterSource mapaerParametrosQuery(AsistenciaMiembroMesaRequestDto filtro) {
	
		Integer mesa = filtro.getMesa() == null ? null : Integer.parseInt(filtro.getMesa());
		
	    return new MapSqlParameterSource("pi_esquema", filtro.getEsquema())
	            .addValue("pi_eleccion", filtro.getIdEleccion())                
	            .addValue("pi_n_centro_computo", filtro.getIdCentroComputo())
	            .addValue("pi_c_ubigeo", filtro.getUbigeo())
	            .addValue("pi_n_mesa", mesa);
	}
	
	private AsistenciaMiembroMesaResponseDto llenarDatos(ResultSet resultSet) throws SQLException{
		return AsistenciaMiembroMesaResponseDto
				.builder()
				.codDescODPE(resultSet.getString("c_nombre_ambito_electoral"))
				.codDescCC(resultSet.getString("c_codigo_centro_computo"))
				.numMesaMadre(resultSet.getString("c_mesa"))
				.descDepartamento(resultSet.getString("c_departamento"))
				.descDistrito(resultSet.getString("c_distrito"))
				.descProvincia(resultSet.getString("c_provincia"))
				.codUbigeo(resultSet.getString("c_codigo_ubigeo"))
				.eleHabil(resultSet.getInt("n_total_electores_habiles"))
				.numEle(resultSet.getString("c_documento_identidad"))
				.votante(resultSet.getString("c_apellido_nombre"))
				.descargo(resultSet.getString("c_cargo"))
				.numero(resultSet.getString("n_nro"))
				.asistencia(resultSet.getString("c_asistencia"))
				.tipoMiembro(resultSet.getInt("n_tipo_miembro"))
				.build();
	}
}
