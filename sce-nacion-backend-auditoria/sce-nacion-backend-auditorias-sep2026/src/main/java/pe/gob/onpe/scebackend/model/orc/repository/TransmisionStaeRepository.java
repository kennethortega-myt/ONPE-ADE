package pe.gob.onpe.scebackend.model.orc.repository;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TransmisionStaeRepository {

	private final JdbcTemplate jdbcTemplate;

    public TransmisionStaeRepository(@Qualifier("jdbcTemplateNacion") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public Map<String, Object> insertActaStae(String piEsquema, String piActa, 
            String usuario, boolean piEsDesarrollo) {

    	log.debug("[START] Iniciar la ejecucion de insertActaStae");
    	
        Map<String, Object> resultado = new HashMap<>();
        String sql = "CALL sp_registrar_transmision_acta(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            resultado = jdbcTemplate.execute(sql, (CallableStatementCallback<Map<String, Object>>) cs -> {
            	// Parámetros IN
                cs.setString(1, piEsquema);
                cs.setString(2, piActa);
                cs.setString(3, usuario);
                cs.setBoolean(4, piEsDesarrollo);

                // Parámetros OUT
                cs.registerOutParameter(5, Types.INTEGER);
                cs.registerOutParameter(6, Types.VARCHAR);
                cs.registerOutParameter(7, Types.VARCHAR);
                cs.registerOutParameter(8, Types.VARCHAR);
                cs.registerOutParameter(9, Types.VARCHAR);
                cs.registerOutParameter(10, Types.VARCHAR);
                cs.registerOutParameter(11, Types.INTEGER);
                

                long t0 = System.currentTimeMillis();
                cs.execute();
                long t1 = System.currentTimeMillis();

                Map<String, Object> result = new HashMap<>();
                result.put("po_resultado", cs.getInt(5));
                result.put("po_mensaje", cs.getString(6));
                result.put("po_estado_acta", cs.getString(7));
                result.put("po_estado_acta_resolucion", cs.getString(8));
                result.put("po_estado_computo", cs.getString(9));
                result.put("po_estado_error_material", cs.getString(10));
                result.put("po_tipo_transmision", cs.getInt(11));
                result.put("_tiempo_ms", (t1 - t0));

                return result;

            });

            Integer poResultado = (Integer) resultado.get("po_resultado");
            String poMensaje = (String) resultado.get("po_mensaje");

            log.info("Procedimiento ejecutado - Resultado: {}, Mensaje: {} en {}", 
            		poResultado, 
            		poMensaje, 
            		Long.parseLong(resultado.get("_tiempo_ms").toString())/ 1000.0  );
            log.debug("[END] Registrar sp_registrar_transmision_acta - Éxito");


        } catch (Exception e) {
            log.error("Error al ejecutar sp_registrar_transmision_acta en esquema: {}", piEsquema, e);
            resultado.put("po_resultado", -1);
            resultado.put("po_mensaje", "Error: " + e.getMessage());
        }
        return resultado;
    }

	
}
