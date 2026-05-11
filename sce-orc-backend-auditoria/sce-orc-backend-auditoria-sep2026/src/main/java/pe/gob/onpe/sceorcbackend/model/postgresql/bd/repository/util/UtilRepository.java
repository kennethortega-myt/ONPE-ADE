package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import lombok.extern.log4j.Log4j2;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesParametros;
@Log4j2
@Repository
public class UtilRepository {

  private static final Logger logger = LoggerFactory.getLogger(UtilRepository.class);

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public UtilRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }


  public Integer obtenerCantidadCandidatos(String esquema, Long idActa) {
    String sql = """
        SELECT [esquema].fn_obtener_cantidad_columnas_acta(
            :pi_esquema,
            :pi_acta_id,
            :pi_eleccion_presidencial,
            :pi_eleccion_congresal,
            :pi_eleccion_parlamento,
            :pi_eleccion_diputados,
            :pi_eleccion_senadores_mult,
            :pi_eleccion_senadores_unico)
        """.replace("[esquema]", esquema);

    Integer resultado = namedParameterJdbcTemplate.queryForObject(
        sql,
        mapaerParametrosQuery(esquema, idActa),
        Integer.class
    );

    return (resultado == null || resultado == -1) ? 0 : resultado;
  }


  private SqlParameterSource mapaerParametrosQuery(String esquema, Long idActa) {
    return new MapSqlParameterSource()
        .addValue(ConstantesParametros.PARAMETRO_BD_SP_PI_ESQUEMA, esquema)
        .addValue("pi_acta_id", idActa)
        .addValue("pi_eleccion_presidencial", ConstantesComunes.COD_ELEC_PRE)
        .addValue("pi_eleccion_congresal", ConstantesComunes.COD_ELEC_CONGRESALES)
        .addValue("pi_eleccion_parlamento", ConstantesComunes.COD_ELEC_PAR)
        .addValue("pi_eleccion_diputados", ConstantesComunes.COD_ELEC_DIPUTADO)
        .addValue("pi_eleccion_senadores_mult", ConstantesComunes.COD_ELEC_SENADO_MULTIPLE)
        .addValue("pi_eleccion_senadores_unico", ConstantesComunes.COD_ELEC_SENADO_UNICO);
  }


}
