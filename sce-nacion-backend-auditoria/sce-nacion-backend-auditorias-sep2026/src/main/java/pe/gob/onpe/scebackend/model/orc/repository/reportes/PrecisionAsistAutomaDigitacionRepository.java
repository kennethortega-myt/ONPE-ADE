package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.scebackend.model.dto.reportes.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PrecisionAsistAutomaDigitacionRepository implements IPrecisionAsistAutomaDigitacionRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final String DB_CODIGO_CENTRO_COMPUTO = "c_codigo_centro_computo";
    private static final String DB_NOMBRE_CENTRO_COMPUTO = "c_nombre_centro_computo";

    public PrecisionAsistAutomaDigitacionRepository(@Qualifier("namedParameterJdbcTemplateNacion") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<PrecisionAsistAutomaDigitacionResumenDto> listaPrecisionAsistAutomaResumen(FiltroPrecisionAsistAutomaDigitacionDto filtro) {
        String sql = "select * from fn_reporte_presicion_asistente_automatico_digitacion_resumen(:pi_esquema, :pi_eleccion, :pi_centro_computo)";
        return namedParameterJdbcTemplate.query(sql, mapearParametrosResumenQuery(filtro), this::llenarDatosResumen);
    }

    @Override
    public List<PrecisionAsistAutomaDigitacionDetalleDto> listaPrecisionAsistAutomaDetalle(FiltroPrecisionAsistAutomaDigitacionDto filtro) {
        String sql = "select * from fn_reporte_presicion_asistente_automatico_digitacion_det(:pi_esquema, :pi_eleccion, :pi_centro_computo)";
        return namedParameterJdbcTemplate.query(sql, mapearParametrosDetalleQuery(filtro), this::llenarDatosDetalle);
    }

    @Override
    public List<PrecisionAsistAutomaDigitacionDetalleDto> listaPrecisionAsistAutomaDetallePref(FiltroPrecisionAsistAutomaDigitacionDto filtro) {
        String sql = "select * from fn_reporte_presicion_asistente_automatico_digitacion_pref(:pi_esquema, :pi_eleccion, :pi_centro_computo)";
        return namedParameterJdbcTemplate.query(sql, mapearParametrosDetalleQuery(filtro), this::llenarDatosDetallePref);
    }

    private SqlParameterSource mapearParametrosResumenQuery(FiltroPrecisionAsistAutomaDigitacionDto filtro) {
        return new MapSqlParameterSource("pi_esquema",filtro.getEsquema())
                .addValue("pi_eleccion",filtro.getIdEleccion())
                .addValue("pi_centro_computo",filtro.getIdCentroComputo());
    }

    private SqlParameterSource mapearParametrosDetalleQuery(FiltroPrecisionAsistAutomaDigitacionDto filtro) {
        return new MapSqlParameterSource("pi_esquema",filtro.getEsquema())
                .addValue("pi_eleccion",filtro.getIdEleccion())
                .addValue("pi_centro_computo",filtro.getIdCentroComputo());
    }

    private PrecisionAsistAutomaDigitacionResumenDto llenarDatosResumen(ResultSet resultSet, int i) throws SQLException {
        return PrecisionAsistAutomaDigitacionResumenDto.builder()
                .nombreEleccion(resultSet.getString("c_nombre_eleccion"))
                .codigoCentroComputo(resultSet.getString(DB_CODIGO_CENTRO_COMPUTO))
                .nombreCentroComputo(resultSet.getString(DB_NOMBRE_CENTRO_COMPUTO))
                .actasConVotoAutomatico(resultSet.getInt("n_actas_con_voto_automatico"))
                .actasSinVotoAutomatico(resultSet.getInt("n_actas_sin_voto_automatico"))
                .actasUnaDigitacion(resultSet.getInt("n_actas_una_digitacion"))
                .actasDosDigitacion(resultSet.getInt("n_actas_dos_digitacion"))
                .actasPendientes(resultSet.getInt("n_actas_pendientes"))
                .actasExtraviadas(resultSet.getInt("n_actas_extraviadas"))
                .actasCorregidas(resultSet.getInt("n_actas_corregidas"))
                .totalActas(resultSet.getInt("n_total_actas"))
                .build();
    }

    private PrecisionAsistAutomaDigitacionDetalleDto llenarDatosDetalle(ResultSet resultSet, int i) throws SQLException {
        return PrecisionAsistAutomaDigitacionDetalleDto.builder()
                .idEeleccion(resultSet.getInt("n_eleccion"))
                .centroComputoId(resultSet.getInt("n_centro_computo"))
                .centroComputoCodigo(resultSet.getString(DB_CODIGO_CENTRO_COMPUTO))
                .centroComputoNombre(resultSet.getString(DB_NOMBRE_CENTRO_COMPUTO))
                .acta(resultSet.getString("c_acta"))
                .coincideVotos(resultSet.getInt("n_coincide_votos"))
                .noCoincideVotos(resultSet.getInt("n_no_coincide_votos"))
                .totalVotos(resultSet.getInt("n_total_votos"))
                .coincideCvas(resultSet.getInt("n_coincide_cvas"))
                .build();
    }

    private PrecisionAsistAutomaDigitacionDetalleDto llenarDatosDetallePref(ResultSet resultSet, int i) throws SQLException {
        return PrecisionAsistAutomaDigitacionDetalleDto.builder()
                .idEeleccion(resultSet.getInt("n_eleccion"))
                .centroComputoId(resultSet.getInt("n_centro_computo"))
                .centroComputoCodigo(resultSet.getString(DB_CODIGO_CENTRO_COMPUTO))
                .centroComputoNombre(resultSet.getString(DB_NOMBRE_CENTRO_COMPUTO))
                .acta(resultSet.getString("c_acta"))
                .coincideVotos(resultSet.getInt("n_coincide_votos"))
                .noCoincideVotos(resultSet.getInt("n_no_coincide_votos"))
                .totalVotos(resultSet.getInt("n_total_votos"))
                .coincidePreferencial(resultSet.getInt("n_coincide_preferencial"))
                .noCoincidePreferencial(resultSet.getInt("n_no_coincide_preferencial"))
                .totalPreferencial(resultSet.getInt("n_total_preferencial"))
                .coincideCvas(resultSet.getInt("n_coincide_cvas"))
                .build();
    }
}
