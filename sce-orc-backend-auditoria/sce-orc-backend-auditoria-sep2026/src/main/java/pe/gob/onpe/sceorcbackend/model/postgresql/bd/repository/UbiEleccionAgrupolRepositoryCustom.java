package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.UbiEleccionAgrupolDto;
import pe.gob.onpe.sceorcbackend.model.dto.request.UbiEleccionAgrupolRequestDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UbiEleccionAgrupolRepositoryCustom implements IUbiEleccionAgrupolRepositoryCustom {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UbiEleccionAgrupolRepositoryCustom(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Integer obtenerCantidadAgrupacionPreferencial(UbiEleccionAgrupolRequestDto filtro) {
        List<UbiEleccionAgrupolDto> lista = new ArrayList<>();
        UbiEleccionAgrupolDto dato = new UbiEleccionAgrupolDto();
        Integer cantidadAgrupacion = 0;
        String sql = "SELECT * FROM [esquema].fn_obtener_datos_cantidad_agrupacion_preferencial(:c_esquema, :n_eleccion, :c_ubigeo, :c_mesa)".replace("[esquema]",filtro.getEsquema());
        lista = namedParameterJdbcTemplate.query(sql, mapaerParametros(filtro), (resultSet, i) -> llenarDatos(resultSet));
        if(!lista.isEmpty()) {
            cantidadAgrupacion = lista.get(0).getCantidadAgrupacionPreferencial();
        }

        return cantidadAgrupacion;
    }

    private SqlParameterSource mapaerParametros(UbiEleccionAgrupolRequestDto filtro) {
        return new MapSqlParameterSource("c_esquema",filtro.getEsquema())
                .addValue("n_eleccion",filtro.getIdEleccion())
                .addValue("c_ubigeo",filtro.getUbigeo())
                .addValue("c_mesa",filtro.getMesa());
    }

    private static UbiEleccionAgrupolDto llenarDatos(ResultSet rs) throws SQLException {
        UbiEleccionAgrupolDto ubiEleccionAgrupolDto = new UbiEleccionAgrupolDto();
        ubiEleccionAgrupolDto.setCantidadAgrupacionPreferencial(rs.getInt("n_cantidad_agrupacion"));
        ubiEleccionAgrupolDto.setUbigeo(rs.getString("c_ubigeo"));
        return ubiEleccionAgrupolDto;
    }
}
