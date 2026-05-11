package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasObservacionesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteAvanceDigitalizacionLeDto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Repository
public class ReporteAvanceDigitalizacionLeRepository implements IReporteAvanceDigitalizacionLeRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReporteAvanceDigitalizacionLeRepository(@Qualifier("jdbcTemplateNacion") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ReporteAvanceDigitalizacionLeDto> listarReporteAvanceDigitalizacionLe(ReporteMesasObservacionesRequestDto filtro) {
        String sql = "SELECT * FROM fn_reporte_avance_digitalizacion_lista_electores(?, ?, ?, ?)";
        return jdbcTemplate.execute(sql, (PreparedStatement ps) -> {

            mapaerParametros(filtro, ps);

            try (ResultSet rs = ps.executeQuery()) {
                List<ReporteAvanceDigitalizacionLeDto> lista = new ArrayList<>();
                while (rs.next()) {
                    llenarDatos(rs, lista);
                }
                return lista;
            }
        });
    }

    private static void mapaerParametros(ReporteMesasObservacionesRequestDto filtro, PreparedStatement ps) throws SQLException {
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
        if(filtro.getUbigeo() == null) {
            ps.setNull(4, Types.NULL);
        } else {
            ps.setObject(4, filtro.getUbigeo());
        }

    }

    private static void llenarDatos(ResultSet rs, List<ReporteAvanceDigitalizacionLeDto> lista) throws SQLException {
        ReporteAvanceDigitalizacionLeDto reporte = new ReporteAvanceDigitalizacionLeDto();
        reporte.setNombreEleccion(rs.getString("c_nombre_eleccion"));
        reporte.setNombreAmbitoElectoral(rs.getString("c_nombre_ambito_electoral"));
        reporte.setCodigoCentroComputo(rs.getString("c_codigo_centro_computo"));
        reporte.setNombreCentroComputo(rs.getString("c_nombre_centro_computo"));
        reporte.setCodigoUbigeo(rs.getString("c_codigo_ubigeo"));
        reporte.setDepartamento(rs.getString("c_departamento"));
        reporte.setProvincia(rs.getString("c_provincia"));
        reporte.setDistrito(rs.getString("c_distrito"));
        reporte.setEstadoDigitalizacion(rs.getString("c_estado_digitalizacion"));
        reporte.setMesa(rs.getString("c_mesa"));
        lista.add(reporte);
    }

}
