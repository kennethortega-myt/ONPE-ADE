package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasObservacionesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteMesasObservacionesDto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Repository
public class ReporteMesasObservacionesRepository implements IReporteMesasObservacionesRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReporteMesasObservacionesRepository(@Qualifier("jdbcTemplateNacion") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ReporteMesasObservacionesDto> listarReporteMesasObservaciones(ReporteMesasObservacionesRequestDto filtro) {
        String sql = "SELECT * FROM fn_reporte_mesas_con_observaciones(?, ?, ?)";
        return jdbcTemplate.execute(sql, (PreparedStatement ps) -> {

            mapaerParametros(filtro, ps);

            try (ResultSet rs = ps.executeQuery()) {
                List<ReporteMesasObservacionesDto> lista = new ArrayList<>();
                while (rs.next()) {
                    llenarDatos(rs, lista);
                }
                return lista;
            }
        });
    }

    private static void mapaerParametros(ReporteMesasObservacionesRequestDto filtro, PreparedStatement ps) throws SQLException {
          ps.setString(1, filtro.getEsquema());
          if(filtro.getIdCentroComputo() == null) {
              ps.setNull(2, Types.NULL);
          } else {
              ps.setInt(2, filtro.getIdCentroComputo());
          }
          if(filtro.getUbigeo() == null) {
              ps.setNull(3, Types.NULL);
          } else {
              ps.setObject(3, filtro.getUbigeo());
          }

    }

    private static void llenarDatos(ResultSet rs, List<ReporteMesasObservacionesDto> lista) throws SQLException {
        ReporteMesasObservacionesDto reporte = new ReporteMesasObservacionesDto();
        reporte.setCodigoAmbitoElectoral(rs.getString("c_codigo_ambito_electoral"));
        reporte.setCodigoCentroComputo(rs.getString("c_codigo_centro_computo"));
        reporte.setDepartamento(rs.getString("c_departamento"));
        reporte.setProvincia(rs.getString("c_provincia"));
        reporte.setDistrito(rs.getString("c_distrito"));
        reporte.setCodigoUbigeo(rs.getString("c_codigo_ubigeo"));
        reporte.setMesa(rs.getString("c_mesa"));
        reporte.setNombreDocumento(rs.getString("c_nombre_documento"));
        reporte.setPagina(rs.getString("n_pagina"));
        reporte.setDescripcionObservacion(rs.getString("c_descripcion_observacion"));
        reporte.setNombreDocumento(rs.getString("c_nombre_documento"));
        lista.add(reporte);
    }
}
