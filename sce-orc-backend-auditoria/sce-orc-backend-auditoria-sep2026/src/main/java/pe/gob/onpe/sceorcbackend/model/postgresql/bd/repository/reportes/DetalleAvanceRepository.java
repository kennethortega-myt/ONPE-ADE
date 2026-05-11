package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroDetalleAvanceDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.DetalleAvanceMiembrosMesaEscrutinioDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.DetalleAvancePersonerosDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DetalleAvanceRepository implements IDetalleAvanceRepository{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DetalleAvanceRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<DetalleAvanceMiembrosMesaEscrutinioDto> listaDetalleAvanceMiembrosMesaEscrutinio(FiltroDetalleAvanceDto filtro) throws SQLException {
        String sql ="SELECT " +
                "c_codigo_ambito, " +
                "c_codigo_centro_computo, " +
                "c_codigo_ubigeo, " +
                "c_departamento, " +
                "c_provincia, " +
                "c_distrito, " +
                "c_procesada, " +
                "c_mesa, " +
                "n_total_mesa_procesadas, " +
                "n_total_mesas, " +
                "(n_total_mesas-n_total_mesa_procesadas) AS n_mesa_sin_procesar, " +
                "(cast(n_total_mesa_procesadas AS decimal)/n_total_mesas) AS n_porcentaje_avance " +
                "FROM fn_reporte_avance_registro_ubigeo_mm_escrutinio(:pi_esquema, :pi_ambito, :pi_centro_computo, :pi_ubigeo)";
        return this.namedParameterJdbcTemplate.query(sql, mapearParametrosQuery(filtro), (resultSet, i) -> llenarDatosMiembrosMesaEscrutinio(resultSet));
    }

    @Override
    public List<DetalleAvancePersonerosDto> listaDetalleAvancePersoneros(FiltroDetalleAvanceDto filtro) throws SQLException {
        String sql ="SELECT " +
                "c_codigo_ambito, " +
                "c_codigo_centro_computo, " +
                "c_codigo_ubigeo, " +
                "c_departamento, " +
                "c_provincia, " +
                "c_distrito, " +
                "c_procesada, " +
                "c_mesa, " +
                "n_total_mesa_procesadas, " +
                "n_total_mesas, " +
                "(n_total_mesas-n_total_mesa_procesadas) AS n_mesa_sin_procesar, " +
                "(cast(n_total_mesa_procesadas AS decimal)/n_total_mesas) AS n_porcentaje_avance " +
                "FROM fn_reporte_avance_registro_ubigeo_personeros(:pi_esquema, :pi_ambito, :pi_centro_computo, :pi_ubigeo)";
        return this.namedParameterJdbcTemplate.query(sql, mapearParametrosQuery(filtro), (resultSet, i) -> llenarDatosPersoneros(resultSet));
    }

    private SqlParameterSource mapearParametrosQuery(FiltroDetalleAvanceDto filtro) {
        return new MapSqlParameterSource("pi_esquema",filtro.getEsquema())
                .addValue("pi_ambito",filtro.getIdAmbitoElectoral())
                .addValue("pi_centro_computo",filtro.getIdCentroComputo())
                .addValue("pi_ubigeo",filtro.getUbigeo());
    }

    private DetalleAvanceMiembrosMesaEscrutinioDto llenarDatosMiembrosMesaEscrutinio(ResultSet rs) throws SQLException {
        DetalleAvanceMiembrosMesaEscrutinioDto reporte = new DetalleAvanceMiembrosMesaEscrutinioDto();
        reporte.setCodigoAmbitoElectoral(rs.getString("c_codigo_ambito"));
        reporte.setCodigoCentroComputo(rs.getString("c_codigo_centro_computo"));
        reporte.setCodigoUbigeo(rs.getString("c_codigo_ubigeo"));
        reporte.setDepartamento(rs.getString("c_departamento"));
        reporte.setProvincia(rs.getString("c_provincia"));
        reporte.setDistrito(rs.getString("c_distrito"));
        reporte.setProcesada(rs.getString("c_procesada"));
        reporte.setMesa(rs.getString("c_mesa"));
        reporte.setTotalMesasProcesadas(rs.getInt("n_total_mesa_procesadas"));
        reporte.setTotalMesas(rs.getInt("n_total_mesas"));
        reporte.setTotalMesaSinProcesar(rs.getInt("n_mesa_sin_procesar"));
        reporte.setPorcentajeAvance(rs.getDouble("n_porcentaje_avance"));
        return reporte;
    }

    private DetalleAvancePersonerosDto llenarDatosPersoneros(ResultSet rs) throws SQLException {
        DetalleAvancePersonerosDto reporte = new DetalleAvancePersonerosDto();
        reporte.setCodigoAmbitoElectoral(rs.getString("c_codigo_ambito"));
        reporte.setCodigoCentroComputo(rs.getString("c_codigo_centro_computo"));
        reporte.setCodigoUbigeo(rs.getString("c_codigo_ubigeo"));
        reporte.setDepartamento(rs.getString("c_departamento"));
        reporte.setProvincia(rs.getString("c_provincia"));
        reporte.setDistrito(rs.getString("c_distrito"));
        reporte.setProcesada(rs.getString("c_procesada"));
        reporte.setMesa(rs.getString("c_mesa"));
        reporte.setTotalMesasProcesadas(rs.getInt("n_total_mesa_procesadas"));
        reporte.setTotalMesas(rs.getInt("n_total_mesas"));
        reporte.setTotalMesaSinProcesar(rs.getInt("n_mesa_sin_procesar"));
        reporte.setPorcentajeAvance(rs.getDouble("n_porcentaje_avance"));
        return reporte;
    }
}
