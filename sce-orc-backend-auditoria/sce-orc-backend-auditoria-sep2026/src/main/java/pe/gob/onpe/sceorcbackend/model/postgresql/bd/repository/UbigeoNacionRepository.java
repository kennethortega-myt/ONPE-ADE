package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.NivelUbigeoDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.UbigeoRequestDto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UbigeoNacionRepository implements IUbigeoNacionRepository {
    private final JdbcTemplate jdbcTemplateReportes;

    @Autowired
    public UbigeoNacionRepository(JdbcTemplate jdbcTemplateReportes) {
        this.jdbcTemplateReportes = jdbcTemplateReportes;
    }


    @Override
    public List<NivelUbigeoDto> listarNivelUbigeUnoPorCentroComputo(UbigeoRequestDto filtro) {
        String sql = "SELECT * FROM fn_obtener_datos_ubigeo_nivel_01_x_eleccion_y_centro_computo(?,?,?)";
        return jdbcTemplateReportes.execute(sql, (PreparedStatement ps) -> {

            mapaerParametrosNivelUbigeoUnoPorCentroComputo(filtro, ps);

            try (ResultSet rs = ps.executeQuery()) {
                List<NivelUbigeoDto> lista = new ArrayList<>();
                while (rs.next()) {
                    llenarDatosNivelUbigeoUnoPorCentroComputo(rs, lista);
                }
                return lista;
            }
        });
    }

    @Override
    public List<NivelUbigeoDto> listarNivelUbigeUnoPorAmbitoElectoral(UbigeoRequestDto filtro) {
        String sql = "SELECT * FROM fn_obtener_datos_ubigeo_nivel_01(?,?,?)";
        return jdbcTemplateReportes.execute(sql, (PreparedStatement ps) -> {

            mapaerParametrosNivelUbigeoUnoPorAmbitoElectoral(filtro, ps);

            try (ResultSet rs = ps.executeQuery()) {
                List<NivelUbigeoDto> lista = new ArrayList<>();
                while (rs.next()) {
                    llenarDatosNivelUbigeoUnoPorCentroComputo(rs, lista);
                }
                return lista;
            }
        });
    }

    @Override
    public List<NivelUbigeoDto> listarNivelUbigeDosPorNivelUno(UbigeoRequestDto filtro) {
        String sql = "SELECT * FROM fn_obtener_datos_ubigeo_nivel_dos_x_ubigeo_nivel_uno(?,?,?,?)";
        return jdbcTemplateReportes.execute(sql, (PreparedStatement ps) -> {

            mapaerParametrosNivelUbigeoDos(filtro, ps);

            try (ResultSet rs = ps.executeQuery()) {
                List<NivelUbigeoDto> lista = new ArrayList<>();
                while (rs.next()) {
                    llenarDatosNivelUbigeo(rs, lista);
                }
                return lista;
            }
        });
    }

    @Override
    public List<NivelUbigeoDto> listarNivelUbigeTresPorNivelDos(UbigeoRequestDto filtro) {
        String sql = "SELECT * FROM fn_obtener_datos_ubigeo_nivel_tres_x_ubigeo_nivel_dos(?,?,?,?)";
        return jdbcTemplateReportes.execute(sql, (PreparedStatement ps) -> {

            mapaerParametrosNivelUbigeoDos(filtro, ps);

            try (ResultSet rs = ps.executeQuery()) {
                List<NivelUbigeoDto> lista = new ArrayList<>();
                while (rs.next()) {
                    llenarDatosNivelUbigeo(rs, lista);
                }
                return lista;
            }
        });
    }
    
    @Override
    public List<NivelUbigeoDto> listarNivelUbigeUnoDistritoElecXambito(UbigeoRequestDto filtro) {
        String sql = "SELECT * FROM fn_obtener_datos_ubigeo_nivel_01_distrito_electoral_x_ambito(?, ?)";
        
        return jdbcTemplateReportes.execute(sql, (PreparedStatement ps) -> {

        	mapaerParametrosNivelUbigeoUnoDistritoElec(filtro, ps);

            try (ResultSet rs = ps.executeQuery()) {
                List<NivelUbigeoDto> lista = new ArrayList<>();
                while (rs.next()) {
                	llenarDatosNivelUbigeoUnoDistritoElec(rs, lista);
                }
                return lista;
            }
        });
    }
    
    private static void mapaerParametrosNivelUbigeoUnoDistritoElec(UbigeoRequestDto filtro, PreparedStatement ps) throws SQLException {
    	ps.setString(1, filtro.getEsquema());
        ps.setInt(2, filtro.getIdAmbitoElectoral());
    }

    private static void llenarDatosNivelUbigeoUnoDistritoElec(ResultSet rs, List<NivelUbigeoDto> lista) throws SQLException {
        NivelUbigeoDto dato = new NivelUbigeoDto();
        dato.setId(rs.getLong("n_distrito_electoral"));
        dato.setCodigo(rs.getString("c_codigo_distrito_electoral"));
        dato.setNombre(rs.getString("c_nombre_distrito_electoral"));
        
        lista.add(dato);
    }
    
    @Override
    public List<NivelUbigeoDto> listarNivelUbigeDosPorDistritoElec(UbigeoRequestDto filtro) {
        String sql = "SELECT * FROM fn_obtener_datos_ubigeo_nivel_02_provincia_x_distrito_electoral(?, ?, ?, ?)";
        
        return jdbcTemplateReportes.execute(sql, (PreparedStatement ps) -> {

        	mapaerParametrosNivelUbigeoDosXdistritoElec(filtro, ps);

            try (ResultSet rs = ps.executeQuery()) {
                List<NivelUbigeoDto> lista = new ArrayList<>();
                while (rs.next()) {
                	llenarDatosNivelUbigeoDosXdistritoElec(rs, lista);
                }
                return lista;
            }
        });
    }

    private static void mapaerParametrosNivelUbigeoDosXdistritoElec(UbigeoRequestDto filtro, PreparedStatement ps) throws SQLException {
    	ps.setString(1, filtro.getEsquema());
        ps.setInt(2, filtro.getIdUbigePadre());
        ps.setInt(3, filtro.getIdAmbitoElectoral());
        ps.setInt(4, filtro.getIdCentroComputo());
    }

    private static void llenarDatosNivelUbigeoDosXdistritoElec(ResultSet rs, List<NivelUbigeoDto> lista) throws SQLException {
        NivelUbigeoDto dato = new NivelUbigeoDto();
        dato.setId(rs.getLong("n_ubigeo_nivel_02"));
        dato.setCodigo(rs.getString("c_ubigeo"));
        dato.setNombre(rs.getString("c_nombre_ubigeo_nivel_02"));
        lista.add(dato);
    }

    private static void mapaerParametrosNivelUbigeoUnoPorCentroComputo(UbigeoRequestDto filtro, PreparedStatement ps) throws SQLException {
        ps.setString(1, filtro.getEsquema());
        ps.setInt(2, filtro.getIdEleccion());
        ps.setInt(3, filtro.getIdCentroComputo());
    }
    private static void mapaerParametrosNivelUbigeoUnoPorAmbitoElectoral(UbigeoRequestDto filtro, PreparedStatement ps) throws SQLException {
        ps.setString(1, filtro.getEsquema());
        ps.setInt(2, filtro.getIdEleccion());
        ps.setInt(3, filtro.getIdAmbitoElectoral());
    }
    private static void llenarDatosNivelUbigeoUnoPorCentroComputo(ResultSet rs, List<NivelUbigeoDto> lista) throws SQLException {
        NivelUbigeoDto dato = new NivelUbigeoDto();
        dato.setId(rs.getLong("n_ubigeo_nivel_01"));
        dato.setCodigo(rs.getString("c_codigo"));
        dato.setNombre(rs.getString("c_nombre"));
        lista.add(dato);
    }

    private static void mapaerParametrosNivelUbigeo(UbigeoRequestDto filtro, PreparedStatement ps) throws SQLException {
        ps.setString(1, filtro.getEsquema());
        ps.setInt(2, filtro.getIdEleccion());
        ps.setInt(3, filtro.getIdUbigePadre());
    }

    private static void mapaerParametrosNivelUbigeoDos(UbigeoRequestDto filtro, PreparedStatement ps) throws SQLException {
        ps.setString(1, filtro.getEsquema());
        ps.setInt(2, filtro.getIdEleccion());
        ps.setInt(3, filtro.getIdCentroComputo());
        ps.setInt(4, filtro.getIdUbigePadre());
    }

    private static void llenarDatosNivelUbigeo(ResultSet rs, List<NivelUbigeoDto> lista) throws SQLException {
        NivelUbigeoDto dato = new NivelUbigeoDto();
        dato.setId(rs.getLong("n_ubigeo"));
        dato.setCodigo(rs.getString("c_codigo"));
        dato.setNombre(rs.getString("c_nombre"));
        lista.add(dato);
    }


}
