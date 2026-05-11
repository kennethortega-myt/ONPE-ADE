package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.scebackend.model.dto.reportes.ComparacionDigitacionAutomaticaDto;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroComparacionDigitacionAutomaticaDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ComparacionDigitacionAutomaticaRepository implements IComparacionDigitacionAutomaticaRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ComparacionDigitacionAutomaticaRepository(@Qualifier("namedParameterJdbcTemplateNacion") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<ComparacionDigitacionAutomaticaDto> listaComparacionDigitacionAutomatica(FiltroComparacionDigitacionAutomaticaDto filtro) {
        String sql = "select * from fn_reporte_comparacion_digitacion_asistente_automatizado(:pi_esquema, :pi_eleccion, :pi_ambito, :pi_centro_computo, :pi_ubigeo, :pi_mesa)";
        return namedParameterJdbcTemplate.query(sql, mapearParametrosQuery(filtro), this::llenarDatos);
    }

    @Override
    public List<ComparacionDigitacionAutomaticaDto> listaComparacionDigitacionAutomaticaPref(FiltroComparacionDigitacionAutomaticaDto filtro) {
        String sql = "select * from fn_reporte_comparacion_digitacion_asistente_automatizado_pref(:pi_esquema, :pi_eleccion, :pi_ambito, :pi_centro_computo, :pi_ubigeo, :pi_mesa)";
        return namedParameterJdbcTemplate.query(sql, mapearParametrosQuery(filtro), this::llenarDatosPref);
    }

    private SqlParameterSource mapearParametrosQuery(FiltroComparacionDigitacionAutomaticaDto filtro) {
        return new MapSqlParameterSource("pi_esquema",filtro.getEsquema())
                .addValue("pi_eleccion",filtro.getIdEleccion())
                .addValue("pi_ambito",filtro.getIdAmbito())
                .addValue("pi_centro_computo",filtro.getIdCentroComputo())
                .addValue("pi_ubigeo",filtro.getUbigeo())
                .addValue("pi_mesa",filtro.getMesa());
    }

    private ComparacionDigitacionAutomaticaDto llenarDatos(ResultSet resultSet, int i) throws SQLException {
        return ComparacionDigitacionAutomaticaDto.builder()
                .codigoAmbitoElectoral(resultSet.getString("c_codigo_ambito_electoral"))
                .nombreAmbitoElectoral(resultSet.getString("c_nombre_ambito_electoral"))
                .codigoCentroComputo(resultSet.getString("c_codigo_centro_computo"))
                .nombreCentroComputo(resultSet.getString("c_nombre_centro_computo"))
                .codigoUbigeo(resultSet.getString("c_codigo_ubigeo"))
                .departamento(resultSet.getString("c_departamento"))
                .provincia(resultSet.getString("c_provincia"))
                .distrito(resultSet.getString("c_distrito"))
                .mesa(resultSet.getString("c_mesa"))
                .numeroCopia(resultSet.getString("c_numero_copia"))
                .digitoChequeoEscrutinio(resultSet.getString("c_digito_chequeo_escrutinio"))
                .posicion(resultSet.getInt("n_posicion"))
                .descripcion(resultSet.getString("c_descripcion"))
                .votoCiudadanoAutom(formatVoto(resultSet.getString("n_ciudadanos_votaron")))
                .votoCiudadanoManual(formatVoto(resultSet.getString("n_ciudadanos_votaron_manual")))
                .firmaAutom(resultSet.getString("c_firmas_automatico"))
                .firmaManual(resultSet.getString("c_firmas_manual"))
                .votoAutomTotal(resultSet.getString("n_voto_automatico"))
                .votoManualTotal(resultSet.getString("n_voto_manual"))
                .validacionVoto(resultSet.getString("c_validacion_voto"))
                .build();
    }

    private ComparacionDigitacionAutomaticaDto llenarDatosPref(ResultSet resultSet, int i) throws SQLException {
        ComparacionDigitacionAutomaticaDto fila = ComparacionDigitacionAutomaticaDto.builder()
                .codigoAmbitoElectoral(resultSet.getString("c_codigo_ambito_electoral"))
                .nombreAmbitoElectoral(resultSet.getString("c_nombre_ambito_electoral"))
                .codigoCentroComputo(resultSet.getString("c_codigo_centro_computo"))
                .nombreCentroComputo(resultSet.getString("c_nombre_centro_computo"))
                .codigoUbigeo(resultSet.getString("c_codigo_ubigeo"))
                .departamento(resultSet.getString("c_departamento"))
                .provincia(resultSet.getString("c_provincia"))
                .distrito(resultSet.getString("c_distrito"))
                .mesa(resultSet.getString("c_mesa"))
                .numeroCopia(resultSet.getString("c_numero_copia"))
                .digitoChequeoEscrutinio(resultSet.getString("c_digito_chequeo_escrutinio"))
                .posicion(resultSet.getInt("n_posicion"))
                .descripcion(resultSet.getString("c_descripcion"))
                .votoCiudadanoAutom(formatVoto(resultSet.getString("n_ciudadanos_votaron")))
                .votoCiudadanoManual(formatVoto(resultSet.getString("n_ciudadanos_votaron_manual")))
                .firmaAutom(resultSet.getString("c_firmas_automatico"))
                .firmaManual(resultSet.getString("c_firmas_manual"))
                .votoAutomTotal(resultSet.getString("n_voto_automatico_total"))
                .votoManualTotal(resultSet.getString("n_voto_manual_total"))
                .votoAutom1(resultSet.getString("n_voto_automatico_1")).votoManual1(resultSet.getString("n_voto_manual_1"))
                .votoAutom2(resultSet.getString("n_voto_automatico_2")).votoManual2(resultSet.getString("n_voto_manual_2"))
                .votoAutom3(resultSet.getString("n_voto_automatico_3")).votoManual3(resultSet.getString("n_voto_manual_3"))
                .votoAutom4(resultSet.getString("n_voto_automatico_4")).votoManual4(resultSet.getString("n_voto_manual_4"))
                .votoAutom5(resultSet.getString("n_voto_automatico_5")).votoManual5(resultSet.getString("n_voto_manual_5"))
                .votoAutom6(resultSet.getString("n_voto_automatico_6")).votoManual6(resultSet.getString("n_voto_manual_6"))
                .votoAutom7(resultSet.getString("n_voto_automatico_7")).votoManual7(resultSet.getString("n_voto_manual_7"))
                .votoAutom8(resultSet.getString("n_voto_automatico_8")).votoManual8(resultSet.getString("n_voto_manual_8"))
                .votoAutom9(resultSet.getString("n_voto_automatico_9")).votoManual9(resultSet.getString("n_voto_manual_9"))
                .votoAutom10(resultSet.getString("n_voto_automatico_10")).votoManual10(resultSet.getString("n_voto_manual_10"))
                .votoAutom11(resultSet.getString("n_voto_automatico_11")).votoManual11(resultSet.getString("n_voto_manual_11"))
                .votoAutom12(resultSet.getString("n_voto_automatico_12")).votoManual12(resultSet.getString("n_voto_manual_12"))
                .votoAutom13(resultSet.getString("n_voto_automatico_13")).votoManual13(resultSet.getString("n_voto_manual_13"))
                .votoAutom14(resultSet.getString("n_voto_automatico_14")).votoManual14(resultSet.getString("n_voto_manual_14"))
                .votoAutom15(resultSet.getString("n_voto_automatico_15")).votoManual15(resultSet.getString("n_voto_manual_15"))
                .votoAutom16(resultSet.getString("n_voto_automatico_16")).votoManual16(resultSet.getString("n_voto_manual_16"))
                .votoAutom17(resultSet.getString("n_voto_automatico_17")).votoManual17(resultSet.getString("n_voto_manual_17"))
                .votoAutom18(resultSet.getString("n_voto_automatico_18")).votoManual18(resultSet.getString("n_voto_manual_18"))
                .votoAutom19(resultSet.getString("n_voto_automatico_19")).votoManual19(resultSet.getString("n_voto_manual_19"))
                .votoAutom20(resultSet.getString("n_voto_automatico_20")).votoManual20(resultSet.getString("n_voto_manual_20"))
                .votoAutom21(resultSet.getString("n_voto_automatico_21")).votoManual21(resultSet.getString("n_voto_manual_21"))
                .votoAutom22(resultSet.getString("n_voto_automatico_22")).votoManual22(resultSet.getString("n_voto_manual_22"))
                .votoAutom23(resultSet.getString("n_voto_automatico_23")).votoManual23(resultSet.getString("n_voto_manual_23"))
                .votoAutom24(resultSet.getString("n_voto_automatico_24")).votoManual24(resultSet.getString("n_voto_manual_24"))
                .votoAutom25(resultSet.getString("n_voto_automatico_25")).votoManual25(resultSet.getString("n_voto_manual_25"))
                .votoAutom26(resultSet.getString("n_voto_automatico_26")).votoManual26(resultSet.getString("n_voto_manual_26"))
                .votoAutom27(resultSet.getString("n_voto_automatico_27")).votoManual27(resultSet.getString("n_voto_manual_27"))
                .votoAutom28(resultSet.getString("n_voto_automatico_28")).votoManual28(resultSet.getString("n_voto_manual_28"))
                .votoAutom29(resultSet.getString("n_voto_automatico_29")).votoManual29(resultSet.getString("n_voto_manual_29"))
                .votoAutom30(resultSet.getString("n_voto_automatico_30")).votoManual30(resultSet.getString("n_voto_manual_30"))
                .votoAutom31(resultSet.getString("n_voto_automatico_31")).votoManual31(resultSet.getString("n_voto_manual_31"))
                .votoAutom32(resultSet.getString("n_voto_automatico_32")).votoManual32(resultSet.getString("n_voto_manual_32"))
                .build();
        fila.calcularDiferencias();
        return fila;
    }
    
    private String formatVoto(String valor) {
        return (valor == null) ? "#" : valor;
    }
}