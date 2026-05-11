package pe.gob.onpe.scebackend.model.orc.repository.comun;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.scebackend.model.dto.request.comun.DetCatalogoEstructuraRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.DetCatalogoEstructuraResponseDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DetCatalogoEstructuraRepositoryCustom implements IDetCatalogoEstructuraRepositoryCustom {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DetCatalogoEstructuraRepositoryCustom(@Qualifier("namedParameterJdbcTemplateNacion") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }


    @Override
    public List<DetCatalogoEstructuraResponseDto> listarDetalleEstructura(DetCatalogoEstructuraRequestDto filtro) {
        String sql = "SELECT * FROM fn_obtener_filtro(:c_esquema,:n_tipo_reporte)";
        return namedParameterJdbcTemplate.query(sql, mapaerParametros(filtro), (resultSet, i) -> llenarDatos(resultSet));
    }

    private SqlParameterSource mapaerParametros(DetCatalogoEstructuraRequestDto filtro) {
        return new MapSqlParameterSource("c_esquema",filtro.getEsquema())
                .addValue("n_tipo_reporte",filtro.getTipoReporte());
    }

    private DetCatalogoEstructuraResponseDto llenarDatos(ResultSet rs) throws SQLException {
        DetCatalogoEstructuraResponseDto dato = new DetCatalogoEstructuraResponseDto();
        dato.setIdCodigo(rs.getInt("n_codigo"));
        dato.setCodigo(rs.getString("c_codigo"));
        dato.setNombre(rs.getString("c_nombre"));

        return dato;
    }
}
