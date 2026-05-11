package pe.gob.onpe.scebackend.model.repository;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.EleccionDto;
import pe.gob.onpe.scebackend.model.dto.TabVersionDTO;
import pe.gob.onpe.scebackend.model.dto.request.CargaDataRequestDTO;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.utils.LoggingUtil;

import java.sql.*;
import java.util.*;

@Service
@Slf4j
public class Procedures {

  private static final String SERVICE_NAME = "procedure";

  private final JdbcTemplate jdbcTemplate;
  
  private final JdbcTemplate maintenanceJdbcTemplate;

  private final ITabLogTransaccionalService logService;

  Logger logger = LoggerFactory.getLogger(Procedures.class);

  @Autowired
  public Procedures(@Qualifier("jdbcTemplateAdmin") JdbcTemplate jdbcTemplate,
                    @Qualifier("maintenanceJdbcTemplate") JdbcTemplate maintenanceJdbcTemplate,
                    ITabLogTransaccionalService logService) {
    this.jdbcTemplate = jdbcTemplate;
    this.maintenanceJdbcTemplate = maintenanceJdbcTemplate;
    this.logService = logService;
  }

  public boolean executeProcedurecargaBDOnpe(String piDblink, String piEsquemaOrigen, String piEsquemaDestino,
    Integer piConfiguracionElectoral, String piAudUsuarioCreacion) {
    Boolean res = false;
    String sql = "CALL sp_cargar_bdonpe(?, ?, ?, ?, ?, ?, ?)";
    try {
      res = jdbcTemplate.execute(sql, (CallableStatementCallback<Boolean>) cs -> {
        cs.setString(1, piDblink.toLowerCase());
        cs.setString(2, piEsquemaOrigen.toLowerCase());
        cs.setString(3, piEsquemaDestino.toLowerCase());
        cs.setInt(4, piConfiguracionElectoral);
        cs.setString(5, piAudUsuarioCreacion);
        cs.setNull(6, Types.NULL);
        cs.setNull(7, Types.NULL);
        cs.registerOutParameter(6, Types.INTEGER);
        cs.registerOutParameter(7, Types.VARCHAR);
        cs.execute();
        logger.info("Mensaje cargar bdonpe: {}",cs.getString(7));
        return cs.getInt(6) == 1;
      });
    } catch (Exception e) {
      log.error(e.getMessage());
      return false;
    }
    try {
      this.logService.registrarLog(piAudUsuarioCreacion, "executeProcedurecargaBDOnpe", SERVICE_NAME, "Se realizó la carga de datos",
          "", "", 0, 0);
    } catch (Exception e) {
      log.error("Error al registrar log en el inicio de la puesta a cero", e);
    }
    return Boolean.TRUE.equals(res);
  }


  public boolean executeProcedurePuestaCero(String piEsquema, String piAudUsuario) {
    log.info("[START] Puesta a cero - Usando datasource maintenanceJdbcTemplate");
    log.info("Esquema: {}, Usuario: {}", piEsquema, piAudUsuario);

    Boolean res = false;
    String sql = "CALL sp_registrar_puesta_cero(?,?,?,?)";
    
    long startTime = System.currentTimeMillis();
    
    try {
      res = maintenanceJdbcTemplate.execute(sql, (CallableStatementCallback<Boolean>) cs -> {
        cs.setString(1, piEsquema.toLowerCase());
        cs.setString(2, piAudUsuario);
        cs.setNull(3, Types.NULL);
        cs.setNull(4, Types.NULL);
        cs.registerOutParameter(3, Types.INTEGER);
        cs.registerOutParameter(4, Types.VARCHAR);
        
        log.info("Ejecutando SP de puesta a cero...");
        cs.execute();
        
        int resultado = cs.getInt(3);
        String mensaje = cs.getString(4);
        
        long duration = (System.currentTimeMillis() - startTime) / 1000;
        log.info("SP finalizado en {} segundos. Resultado: {}, Mensaje: {}", duration, resultado, mensaje);
        
        return resultado == 1;
      });
    } catch (Exception e) {
      long duration = (System.currentTimeMillis() - startTime) / 1000;
      log.error("Error en puesta a cero después de {} segundos: {}", duration, e.getMessage(), e);
      return false;
    }

    log.info("[END] Puesta a cero - Resultado: {}", res);
    return Boolean.TRUE.equals(res);
  }

  public boolean executeProcedureVerificaVersionPuestaCero(String piEsquema, String piAudUsuario) {
    log.debug("[START] Verifica versión puesta cero");
    Boolean res = false;
    String sql = "CALL sp_verifica_version_puesta_cero(?,?,?,?)";
    try {
      res = jdbcTemplate.execute(sql, (CallableStatementCallback<Boolean>) cs -> {
        cs.setString(1, piEsquema.toLowerCase());
        cs.setString(2, piAudUsuario.toLowerCase());
        cs.setNull(3, Types.NULL);
        cs.setNull(4, Types.NULL);
        cs.registerOutParameter(3, Types.INTEGER);
        cs.registerOutParameter(4, Types.VARCHAR);
        cs.execute();
        return cs.getInt(3) == 1;
      });
    } catch (Exception e) {
      log.error(e.getMessage());
      return false;
    }
    return Boolean.TRUE.equals(res);
  }

  public boolean executeProcedureVerificaVersionRegistro(String piEsquema, String piAudUsuario, String piCadena,
                                                         Integer piActivo, Timestamp piFechaCreacion,
                                                         String piCodVersion, Timestamp piFechaVersion) {
    log.debug("[START] Verifica versión registro");
    Boolean res = false;
    String sql = "CALL sp_verifica_version_registro(?,?,?,?,?,?,?,?,?)";
    try {
      res = jdbcTemplate.execute(sql, (CallableStatementCallback<Boolean>) cs -> {
        cs.setString(1, piEsquema.toLowerCase());
        cs.setString(2, piAudUsuario.toLowerCase());
        cs.setString(3, piCadena.toLowerCase());
        cs.setInt(4, piActivo);
        cs.setTimestamp(5, piFechaCreacion);
        cs.setString(6, piCodVersion.toLowerCase());
        cs.setTimestamp(7, piFechaVersion);

        cs.setNull(8, Types.NULL);
        cs.setNull(9, Types.NULL);
        cs.registerOutParameter(8, Types.INTEGER);
        cs.registerOutParameter(9, Types.VARCHAR);
        cs.execute();
        return cs.getInt(8) == 1;
      });
    } catch (Exception e) {
      log.error(e.getMessage());
      return false;
    }
    return Boolean.TRUE.equals(res);
  }

  public List<TabVersionDTO> executeFunctionVerificaVersionObtenerRegistros(String esquemaDestino) {
    log.debug("[START] Verifica versión obtener registros");

    try {
      String sql = "SELECT * FROM fn_verifica_version_obtener_registros(?)";

      return jdbcTemplate.execute(sql, (PreparedStatement ps) -> {
        ps.setString(1, esquemaDestino);
        try (ResultSet rs = ps.executeQuery()) {
          List<TabVersionDTO> registros = new ArrayList<>();
          while (rs.next()) {
            TabVersionDTO registro = new TabVersionDTO();
            registro.setCadena(rs.getString("c_cadena"));
            registro.setFechaCreacion(rs.getTimestamp("d_aud_fecha_creacion"));
            registros.add(registro);
          }
          return registros;
        }
      });

    } catch (Exception e) {
      log.error("Error al ejecutar el procedimiento almacenado", e);
      throw new GenericException("Error al ejecutar el procedimiento almacenado", e.getMessage());
    }
  }

  public boolean executeUpdateprincipal(CargaDataRequestDTO request) {

    Boolean res = false;
    String sql = "CALL sp_actualizar_eleccion_principal(?,?,?,?,?)";
    try {
      res = jdbcTemplate.execute(sql, (CallableStatementCallback<Boolean>) cs -> {
        cs.setString(1, request.getNombreEsquemaPrincipal().toLowerCase());
        cs.setInt(2, request.getIdProceso());
        cs.setString(3, request.getUsuario().toLowerCase());
        cs.setNull(4, Types.NULL);
        cs.setNull(5, Types.NULL);
        cs.registerOutParameter(4, Types.INTEGER);
        cs.registerOutParameter(5, Types.VARCHAR);
        cs.execute();
        return cs.getInt(4) == 1;
      });
    } catch (Exception e) {
      return false;
    }
    return Boolean.TRUE.equals(res);
  }

  public List<EleccionDto> listarEleccionEsquema(String piEsquema, String piAudUsuario) {

    String sql = "SELECT * FROM fn_listar_elecciones(?, ?)";

    return jdbcTemplate.execute(sql, (PreparedStatement ps) -> {
      ps.setString(1, piEsquema);
      ps.setString(2, piAudUsuario);
      try (ResultSet rs = ps.executeQuery()) {
        List<EleccionDto> elecciones = new ArrayList<>();
        while (rs.next()) {
          EleccionDto eleccion = new EleccionDto();
          eleccion.setId(rs.getLong("n_eleccion_pk"));
          eleccion.setNombre(rs.getString("c_nombre"));
          eleccion.setPrincipal(rs.getInt("n_principal"));
          eleccion.setCodigo(rs.getString("c_codigo"));
          elecciones.add(eleccion);
        }
        return elecciones;
      }
    });
  }

  public boolean cargarEstructuraVistas(String piEsquemaDestino, String piAudUsuarioCreacion) {

    log.debug("[START] cargarEstructuraVistas ");
    Boolean res = false;
    String sql = "CALL sp_cargar_pr_estructura_vistas_elecciones(?,?,?,?)";
    try {
      res = jdbcTemplate.execute(sql, (CallableStatementCallback<Boolean>) cs -> {
        cs.setString(1, piEsquemaDestino.toLowerCase());
        cs.setString(2, piAudUsuarioCreacion);
        cs.setNull(3, Types.NULL);
        cs.setNull(4, Types.NULL);
        cs.registerOutParameter(3, Types.INTEGER);
        cs.registerOutParameter(4, Types.VARCHAR);
        cs.execute();
        return cs.getInt(3) == 1;
      });
      log.debug("[END] cargarEstructuraVistas ");
    } catch (Exception e) {
      LoggingUtil.logTrace("cargarEstructuraVistas", "Procedures.java", Arrays.asList(piEsquemaDestino, piAudUsuarioCreacion), null,
          true, e);

      return false;
    }
    return Boolean.TRUE.equals(res);
  }

  public boolean cargarEstructuraParticipacionCiudadana(String piEsquemaDestino, String piAudUsuarioCreacion) {

    log.debug("[START] cargarEstructuraParticipacionCiudadana ");
    Boolean res = false;
    String sql = "CALL sp_cargar_pr_estructura_participacion_ciudadana(?,?,?,?)";
    try {
      res = jdbcTemplate.execute(sql, (CallableStatementCallback<Boolean>) cs -> {
        cs.setString(1, piEsquemaDestino.toLowerCase());
        cs.setString(2, piAudUsuarioCreacion);
        cs.setNull(3, Types.NULL);
        cs.setNull(4, Types.NULL);
        cs.registerOutParameter(3, Types.INTEGER);
        cs.registerOutParameter(4, Types.VARCHAR);
        cs.execute();
        return cs.getInt(3) == 1;
      });
      log.debug("[END] cargarEstructuraParticipacionCiudadana ");
    } catch (Exception e) {
      LoggingUtil.logTrace("cargarEstructuraParticipacionCiudadana", "Procedures.java",
          Arrays.asList(piEsquemaDestino, piAudUsuarioCreacion), null, true, e);

      return false;
    }
    return Boolean.TRUE.equals(res);
  }

}
