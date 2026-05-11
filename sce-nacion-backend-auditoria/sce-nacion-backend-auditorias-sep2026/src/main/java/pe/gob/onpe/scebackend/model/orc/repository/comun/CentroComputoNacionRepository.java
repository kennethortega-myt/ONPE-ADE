package pe.gob.onpe.scebackend.model.orc.repository.comun;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.scebackend.model.dto.request.comun.CentroComputoRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.CentroComputoDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CentroComputoNacionRepository implements ICentroComputoNacionRepository {
  
    public static final String C_ESQUEMA = "c_esquema";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CentroComputoNacionRepository(@Qualifier("namedParameterJdbcTemplateNacion") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<CentroComputoDto> listarCentroComputo(CentroComputoRequestDto filtro) {
        String sql = "SELECT * FROM fn_obtener_datos_centro_computo(:c_esquema)";
        return namedParameterJdbcTemplate.query(sql, mapaerParametrosCentroComputo(filtro), (resultSet, i) -> llenarDatosCentroComputoPorEleccion(resultSet));
    }

    @Override
    public List<CentroComputoDto> listarCentroComputoPorEleccion(CentroComputoRequestDto filtro) {
        String sql = "SELECT * FROM fn_obtener_datos_centro_computo_x_eleccion(:c_esquema,:n_eleccion)";
        return namedParameterJdbcTemplate.query(sql, mapaerParametrosCentroComputoPorEleccion(filtro), (resultSet, i) -> llenarDatosCentroComputoPorEleccion(resultSet));
    }

    private SqlParameterSource mapaerParametrosCentroComputoPorEleccion(CentroComputoRequestDto filtro) {
        return new MapSqlParameterSource(C_ESQUEMA,filtro.getEsquema())
                .addValue("n_eleccion",filtro.getIdEleccion());
    }

    private CentroComputoDto llenarDatosCentroComputoPorEleccion(ResultSet rs) throws SQLException {
        CentroComputoDto dato = new CentroComputoDto();
        dato.setId(rs.getInt("n_centro_computo"));
        dato.setCodigo(rs.getString("c_codigo"));
        dato.setNombre(rs.getString("c_nombre"));
        return dato;
    }

    @Override
    public List<CentroComputoDto> listarCentroComputoPorAmbitoElectoral(CentroComputoRequestDto filtro) {
        String sql = "SELECT * FROM fn_obtener_datos_centro_computo_x_ambito_electoral(:c_esquema,:n_ambito_electoral)";
        
        return namedParameterJdbcTemplate.query(sql, mapaerParametrosAmbitoElectoral(filtro), (rs, i) -> {
        	CentroComputoDto dato = new CentroComputoDto();
            dato.setId(rs.getInt("n_centro_computo_pk"));
            dato.setCodigo(rs.getString("c_codigo"));
            dato.setNombre(rs.getString("c_nombre"));
            return dato;
        });

    }

    private SqlParameterSource mapaerParametrosAmbitoElectoral(CentroComputoRequestDto filtro) {
        return new MapSqlParameterSource(C_ESQUEMA,filtro.getEsquema())
                .addValue("n_ambito_electoral",filtro.getIdAmbitoElectoral());
    }

    private SqlParameterSource mapaerParametrosCentroComputo(CentroComputoRequestDto filtro) {
        return new MapSqlParameterSource(C_ESQUEMA,filtro.getEsquema());
    }
}
