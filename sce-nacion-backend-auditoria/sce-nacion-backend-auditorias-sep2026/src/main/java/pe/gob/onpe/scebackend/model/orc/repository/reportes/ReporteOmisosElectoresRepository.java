package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteOmisosRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteOmisosDto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Repository
public class ReporteOmisosElectoresRepository{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReporteOmisosElectoresRepository(@Qualifier("jdbcTemplateNacion") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ReporteOmisosDto> listarReporteOmisos(ReporteOmisosRequestDto filtro) {
        String sql = "SELECT * FROM fn_reporte_avance_registro_omisos_electores(?, ?, ?)";
        return jdbcTemplate.execute(sql, (PreparedStatement ps) -> {

            mapaerParametros(filtro, ps);

            try (ResultSet rs = ps.executeQuery()) {
                List<ReporteOmisosDto> lista = new ArrayList<>();
                while (rs.next()) {
                    llenarDatos(rs, lista);
                }
                return lista;
            }
        });
    }

    private static void mapaerParametros(ReporteOmisosRequestDto filtro, PreparedStatement ps) throws SQLException {
          ps.setString(1, filtro.getEsquema());
          if(filtro.getIdEleccion() == null) {
              ps.setNull(2, Types.NULL);
          } else {
              ps.setInt(2, filtro.getIdEleccion());
          }

          if(filtro.getIdCentroComputo() == null) {
              ps.setNull(3, Types.NULL);
          } else {
              ps.setInt(3, filtro.getIdCentroComputo());
          }
        log.debug("Filtro:"+ filtro);
        log.debug("Sentencia:" + ps);
    }

    private static void llenarDatos(ResultSet rs, List<ReporteOmisosDto> lista) throws SQLException {
        ReporteOmisosDto reporte = new ReporteOmisosDto();
        reporte.setCodigoUbigeo(rs.getString("c_codigo_ubigeo"));
        reporte.setCodigoCentroComputo(rs.getString("c_codigo_centro_computo"));
        reporte.setDepartamento(rs.getString("c_departamento"));
        reporte.setProvincia(rs.getString("c_provincia"));
        reporte.setDistrito(rs.getString("c_distrito"));
        reporte.setTotalMesas(rs.getInt("n_total_mesa"));
        reporte.setTotalElectores(rs.getInt("n_votantes"));
        reporte.setTotalMesasRegistradas(rs.getInt("n_mesas_registradas"));
        reporte.setTotalOmisos(rs.getInt("n_total_omisos"));
        lista.add(reporte);
    }
}
