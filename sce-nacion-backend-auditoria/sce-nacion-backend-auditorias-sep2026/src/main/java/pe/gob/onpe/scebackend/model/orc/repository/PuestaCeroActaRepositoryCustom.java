package pe.gob.onpe.scebackend.model.orc.repository;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PuestaCeroActaRepositoryCustom {

	private final JdbcTemplate jdbcTemplate;
	
	@PersistenceContext(unitName = "locationEntityManagerFactory")
	private EntityManager entityManager;
	
    public PuestaCeroActaRepositoryCustom(@Qualifier("jdbcTemplateNacion") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public Map<String, Object> puestaCeroActa(
    		String piEsquema, 
    		String piMesa,
    		Integer piEleccion,
            String usuario) {

    	log.debug("[START] Iniciar la ejecucion de puesta cero por acta");
    	
        Map<String, Object> resultado = new HashMap<>();
        String sql = "CALL sp_registrar_puesta_cero_acta(?, ?, ?, ?, ?, ?)";
        try {
            resultado = jdbcTemplate.execute(sql, (CallableStatementCallback<Map<String, Object>>) cs -> {
            	// Parámetros IN
                cs.setString(1, piEsquema);
                cs.setString(2, piMesa);
                cs.setInt(3, piEleccion);
                cs.setString(4, usuario);

                // Parámetros OUT
                cs.registerOutParameter(5, Types.INTEGER);
                cs.registerOutParameter(6, Types.VARCHAR);
                

                long t0 = System.currentTimeMillis();
                cs.execute();
                long t1 = System.currentTimeMillis();

                Map<String, Object> result = new HashMap<>();
                result.put("po_resultado", cs.getInt(5));
                result.put("po_mensaje", cs.getString(6));
                result.put("_tiempo_ms", (t1 - t0));

                return result;

            });

            Integer poResultado = (Integer) resultado.get("po_resultado");
            String poMensaje = (String) resultado.get("po_mensaje");

            log.info("Procedimiento ejecutado - Resultado: {}, Mensaje: {} en {}", 
            		poResultado, 
            		poMensaje, 
            		Long.parseLong(resultado.get("_tiempo_ms").toString())/ 1000.0  );
            log.debug("[END] Registrar sp_registrar_puesta_cero_acta - Éxito");
            
            if (poResultado != null && poResultado == 1) {
                entityManager.clear();
                log.info("Contexto de persistencia limpiado después de puesta a cero");
            }


        } catch (Exception e) {
            log.error("Error al ejecutar sp_registrar_puesta_cero_acta en esquema: {}", piEsquema, e);
            resultado.put("po_resultado", -1);
            resultado.put("po_mensaje", "Error: " + e.getMessage());
        }
        return resultado;
    }
	
}
