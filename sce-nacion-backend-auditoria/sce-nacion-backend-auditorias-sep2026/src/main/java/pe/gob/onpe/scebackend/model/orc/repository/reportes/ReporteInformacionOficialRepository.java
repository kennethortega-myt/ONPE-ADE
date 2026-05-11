package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteInformacionOficialRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteInformacionOficialDto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Repository
public class ReporteInformacionOficialRepository implements IReporteInformacionOficialRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReporteInformacionOficialRepository(@Qualifier("jdbcTemplateNacion") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ReporteInformacionOficialDto> listarReporteInformacionOficial(ReporteInformacionOficialRequestDto filtro) {
        String sql = "SELECT * FROM fn_reporte_monitoreo_de_informacion_oficial(?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.execute(sql, (PreparedStatement ps) -> {

            mapaerParametros(filtro, ps);

            try (ResultSet rs = ps.executeQuery()) {
                List<ReporteInformacionOficialDto> lista = new ArrayList<>();
                while (rs.next()) {
                    llenarDatos(rs, lista);
                }
                return lista;
            }
        });
    }

    private static void mapaerParametros(ReporteInformacionOficialRequestDto filtro, PreparedStatement ps) throws SQLException {
          ps.setString(1, filtro.getEsquema());
          if(filtro.getIdEleccion() == null) {
              ps.setNull(2, Types.NULL);
          } else {
              ps.setInt(2, filtro.getIdEleccion());
          }

          if(filtro.getIdAmbitoElectoral() == null) {
              ps.setNull(3, Types.NULL);
          } else {
              ps.setString(3, filtro.getCodigoAmbitoElectoral());
          }

          if(filtro.getCodigoCentroComputo() == null) {
              ps.setNull(4, Types.NULL);
          } else {
              ps.setString(4, filtro.getCodigoCentroComputo());
          }

          if(filtro.getCodigoUbigeoNivelUno() == null) {
              ps.setNull(5, Types.NULL);
          } else {
              ps.setObject(5, filtro.getCodigoUbigeoNivelUno());
          }

          if(filtro.getCodigoUbigeoNivelDos() == null) {
              ps.setNull(6, Types.NULL);
          } else {
              ps.setObject(6, filtro.getCodigoUbigeoNivelDos());
          }

          if(filtro.getCodigoUbigeoNivelTres() == null) {
              ps.setNull(7, Types.NULL);
          } else {
              ps.setObject(7, filtro.getCodigoUbigeoNivelTres());
          }

    }

    private static void llenarDatos(ResultSet rs, List<ReporteInformacionOficialDto> lista) throws SQLException {
        ReporteInformacionOficialDto reporte = new ReporteInformacionOficialDto();
        reporte.setCodigoEleccion(rs.getString("n_codigo_eleccion"));
        reporte.setCodigoAmbitoElectoral(rs.getString("c_codigo_ambito"));

        reporte.setCodigoCentroComputo(rs.getString("c_codigo_centro_computo"));
        reporte.setUbigeo(rs.getString("c_ubigeo"));
        reporte.setNivelUbigeoUno(rs.getString("c_departamento"));

        reporte.setNivelUbigeoDos(rs.getString("c_provincia"));
        reporte.setNivelUbigeoTres(rs.getString("c_distrito"));

        reporte.setElectoresHabiles(rs.getLong("n_electores_habiles"));
        reporte.setCiudadanoVotaron(rs.getLong("n_ciudadanos_votaron"));
        reporte.setAusentismoCifras(rs.getLong("n_ciudadanos_no_votaron"));
        reporte.setPorcentajeParticipacion(rs.getDouble("porcentaje_participacion"));
        reporte.setPorcentajeAusentismo(rs.getDouble("porcentaje_ausentismo"));

        lista.add(reporte);
    }
}
