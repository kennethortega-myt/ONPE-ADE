package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.sceorcbackend.model.dto.CentroComputoDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.CentroComputoRequestDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CentroComputoNacionRepository implements ICentroComputoNacionRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CentroComputoNacionRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<CentroComputoDto> listarCentroComputo(CentroComputoRequestDto filtro) {
        String sql = "SELECT * FROM [esquema].fn_obtener_datos_centro_computo(:c_esquema)".replace("[esquema]",filtro.getEsquema());

        return namedParameterJdbcTemplate.query(sql, mapaerParametrosCentroComputo(filtro), (resultSet, i) -> llenarDatosCentroComputoPorEleccion(resultSet));
    }

    @Override
    public List<CentroComputoDto> listarCentroComputoPorEleccion(CentroComputoRequestDto filtro) {
        String sql = "SELECT * FROM [esquema].fn_obtener_datos_centro_computo_x_eleccion(:c_esquema,:n_eleccion)".replace("[esquema]",filtro.getEsquema());
        return namedParameterJdbcTemplate.query(sql, mapaerParametrosCentroComputoPorEleccion(filtro), (resultSet, i) -> llenarDatosCentroComputoPorEleccion(resultSet));
    }

    private SqlParameterSource mapaerParametrosCentroComputoPorEleccion(CentroComputoRequestDto filtro) {
        return new MapSqlParameterSource("c_esquema",filtro.getEsquema())
                .addValue("n_eleccion",filtro.getIdEleccion());
    }

    private CentroComputoDto llenarDatosCentroComputoPorEleccion(ResultSet rs) throws SQLException {
        CentroComputoDto dato = new CentroComputoDto();
        dato.setId(rs.getLong("n_centro_computo"));
        dato.setCodigo(rs.getString("c_codigo"));
        dato.setNombre(rs.getString("c_nombre"));
        return dato;
    }
    
    private CentroComputoDto llenarDatosCentroComputoPorAmbito(ResultSet rs) throws SQLException {
        CentroComputoDto dato = new CentroComputoDto();
        dato.setId(rs.getLong("n_centro_computo_pk"));
        dato.setCodigo(rs.getString("c_codigo"));
        dato.setNombre(rs.getString("c_nombre"));
        return dato;
    }

    @Override
    public List<CentroComputoDto> listarCentroComputoPorAmbitoElectoral(CentroComputoRequestDto filtro) {
        String sql = "SELECT * FROM [esquema].fn_obtener_datos_centro_computo_x_ambito_electoral(:c_esquema,:n_ambito_electoral)".replace("[esquema]",filtro.getEsquema());

        return namedParameterJdbcTemplate.query(sql, mapaerParametrosAmbitoElectoral(filtro), (resultSet, i) -> llenarDatosCentroComputoPorAmbito(resultSet));

    }

    private SqlParameterSource mapaerParametrosAmbitoElectoral(CentroComputoRequestDto filtro) {
        return new MapSqlParameterSource("c_esquema",filtro.getEsquema())
                .addValue("n_ambito_electoral",filtro.getIdAmbitoElectoral());
    }

    private SqlParameterSource mapaerParametrosCentroComputo(CentroComputoRequestDto filtro) {
        return new MapSqlParameterSource("c_esquema",filtro.getEsquema());
    }
}
