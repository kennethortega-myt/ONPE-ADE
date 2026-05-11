package pe.gob.onpe.scebackend.model.orc.repository.comun;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.scebackend.model.dto.request.comun.UbigeoRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.NivelUbigeoDto;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

@Repository
public class UbigeoNacionRepository implements IUbigeoNacionRepository {

    public static final String C_ESQUEMA = "c_esquema";
    public static final String N_ELECCION = "n_eleccion";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UbigeoNacionRepository(@Qualifier("namedParameterJdbcTemplateNacion") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<NivelUbigeoDto> listarNivelUbigeUnoPorCentroComputo(UbigeoRequestDto filtro) {
        String sql = "SELECT * FROM fn_obtener_datos_ubigeo_nivel_01_x_eleccion_y_centro_computo(:c_esquema,:n_eleccion,:n_centro_computo)";
        return namedParameterJdbcTemplate.query(sql, mapaerParametrosNivelUbigeoUnoPorCentroComputo(filtro), (resultSet, i) -> llenarDatosNivelUbigeoUnoPorCentroComputo(resultSet));
    }

    private SqlParameterSource mapaerParametrosNivelUbigeoUnoPorCentroComputo(UbigeoRequestDto filtro) {
        return new MapSqlParameterSource(C_ESQUEMA,filtro.getEsquema())
                .addValue(N_ELECCION,filtro.getIdEleccion())
                .addValue("n_centro_computo",filtro.getIdCentroComputo());
    }

    private NivelUbigeoDto llenarDatosNivelUbigeoUnoPorCentroComputo(ResultSet rs) throws SQLException {
        NivelUbigeoDto dato = new NivelUbigeoDto();
        dato.setId(rs.getLong("n_ubigeo_nivel_01"));
        dato.setCodigo(rs.getString("c_codigo"));
        dato.setNombre(rs.getString("c_nombre"));
        return dato;
    }


    @Override
    public List<NivelUbigeoDto> listarNivelUbigeUnoPorAmbitoElectoral(UbigeoRequestDto filtro) {
        String sql = "SELECT * FROM fn_obtener_datos_ubigeo_nivel_01(:c_esquema,:n_eleccion,:n_ambito_electoral)";
        return namedParameterJdbcTemplate.query(sql, mapaerParametrosNivelUbigeoUnoPorAmbitoElectoral(filtro), (resultSet, i) -> llenarDatosNivelUbigeoUnoPorCentroComputo(resultSet));

    }

    private SqlParameterSource mapaerParametrosNivelUbigeoUnoPorAmbitoElectoral(UbigeoRequestDto filtro) {
        return new MapSqlParameterSource(C_ESQUEMA,filtro.getEsquema())
                .addValue(N_ELECCION,filtro.getIdEleccion())
                .addValue("n_ambito_electoral",filtro.getIdAmbitoElectoral());
    }

    @Override
    public List<NivelUbigeoDto> listarNivelUbigeDosPorNivelUno(UbigeoRequestDto filtro) {
        String sql = "SELECT * FROM fn_obtener_datos_ubigeo_nivel_dos_x_ubigeo_nivel_uno(:c_esquema,:n_eleccion,:n_centro_computo,:n_ubigeo_padre)";
        return namedParameterJdbcTemplate.query(sql, mapaerParametrosNivelUbigeoDos(filtro), (resultSet, i) -> llenarDatosNivelUbigeo(resultSet));
    }

    private SqlParameterSource mapaerParametrosNivelUbigeoDos(UbigeoRequestDto filtro) {

        return new MapSqlParameterSource(C_ESQUEMA,filtro.getEsquema())
                .addValue(N_ELECCION,filtro.getIdEleccion())
                .addValue("n_centro_computo",filtro.getIdCentroComputo())
                .addValue("n_ubigeo_padre",filtro.getIdUbigePadre());
    }

    private NivelUbigeoDto llenarDatosNivelUbigeo(ResultSet rs) throws SQLException {
        NivelUbigeoDto dato = new NivelUbigeoDto();
        dato.setId(rs.getLong("n_ubigeo"));
        dato.setCodigo(rs.getString("c_codigo"));
        dato.setNombre(rs.getString("c_nombre"));
        return dato;
    }

    @Override
    public List<NivelUbigeoDto> listarNivelUbigeTresPorNivelDos(UbigeoRequestDto filtro) {
        String sql = "SELECT * FROM fn_obtener_datos_ubigeo_nivel_tres_x_ubigeo_nivel_dos(:c_esquema,:n_eleccion,:n_centro_computo,:n_ubigeo_padre)";

        return namedParameterJdbcTemplate.query(sql, mapaerParametrosNivelUbigeoDos(filtro), (resultSet, i) -> llenarDatosNivelUbigeo(resultSet));

    }
    
    @Override
    public List<NivelUbigeoDto> listarNivelUbigeUnoDistritoElecXambito(UbigeoRequestDto filtro) {
        String sql = "SELECT * FROM fn_obtener_datos_ubigeo_nivel_01_distrito_electoral_x_ambito(:c_esquema,:n_ambito_electoral)";
        return namedParameterJdbcTemplate.query(
        		sql, 
        		mapaerParametrosNivelUbigeoUnoDistritoElec(filtro), 
        		(resultSet, i) -> llenarDatosNivelUbigeoUnoDistritoElec(resultSet));
    }
    
    private SqlParameterSource mapaerParametrosNivelUbigeoUnoDistritoElec(UbigeoRequestDto filtro) {
        return new MapSqlParameterSource(C_ESQUEMA, filtro.getEsquema())
                .addValue("n_ambito_electoral",filtro.getIdAmbitoElectoral());
    }

    private NivelUbigeoDto llenarDatosNivelUbigeoUnoDistritoElec(ResultSet rs) throws SQLException {
        NivelUbigeoDto dato = new NivelUbigeoDto();
        dato.setId(rs.getLong("n_distrito_electoral"));
        dato.setCodigo(rs.getString("c_codigo_distrito_electoral"));
        dato.setNombre(rs.getString("c_nombre_distrito_electoral"));
        return dato;
    }
    
    @Override
    public List<NivelUbigeoDto> listarNivelUbigeDosPorDistritoElec(UbigeoRequestDto filtro) {
        String sql = "SELECT * FROM fn_obtener_datos_ubigeo_nivel_02_provincia_x_distrito_electoral(:c_esquema, :pi_n_distrito_electoral, :pi_n_ambito_electoral, :pi_n_centro_computo)";
        return namedParameterJdbcTemplate.query(
        		sql, 
        		mapaerParametrosNivelUbigeoDosXdistritoElec(filtro), 
        		(resultSet, i) -> llenarDatosNivelUbigeoDosXdistritoElec(resultSet));
    }

    private SqlParameterSource mapaerParametrosNivelUbigeoDosXdistritoElec(UbigeoRequestDto filtro) {

        return new MapSqlParameterSource(C_ESQUEMA, filtro.getEsquema())
                .addValue("pi_n_distrito_electoral", filtro.getIdUbigePadre())
                .addValue("pi_n_ambito_electoral", filtro.getIdAmbitoElectoral())
                .addValue("pi_n_centro_computo", filtro.getIdCentroComputo());
    }

    private NivelUbigeoDto llenarDatosNivelUbigeoDosXdistritoElec(ResultSet rs) throws SQLException {
        NivelUbigeoDto dato = new NivelUbigeoDto();
        dato.setId(rs.getLong("n_ubigeo_nivel_02"));
        dato.setCodigo(rs.getString("c_ubigeo"));
        dato.setNombre(rs.getString("c_nombre_ubigeo_nivel_02"));
        return dato;
    }


}
