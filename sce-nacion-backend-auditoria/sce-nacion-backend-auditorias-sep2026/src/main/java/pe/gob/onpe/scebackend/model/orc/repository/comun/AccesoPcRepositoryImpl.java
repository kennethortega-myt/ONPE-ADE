package pe.gob.onpe.scebackend.model.orc.repository.comun;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AccesoPcRepositoryImpl implements IAccesoPcRepositoryCustom{

    private final JdbcTemplate jdbcTemplate;

    public AccesoPcRepositoryImpl(@Qualifier("jdbcTemplateNacion") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Object> executeRegistrarAccesoPc(String piEsquema, String piUsuario, String piIp) {
        log.debug("[START] Registrar acceso PC - Usuario: {}, IP: {}, Esquema: {}", piUsuario, piIp, piEsquema);

        Map<String, Object> resultado = new HashMap<>();
        String sql = "CALL sp_registrar_acceso_pc(?, ?, ?, ?, ?)";
        try {
            resultado = jdbcTemplate.execute(sql, (CallableStatementCallback<Map<String, Object>>) cs -> {
                cs.setString(1, piEsquema);
                cs.setString(2, piUsuario);
                cs.setString(3, piIp);

                cs.registerOutParameter(4, Types.INTEGER);
                cs.registerOutParameter(5, Types.VARCHAR);

                cs.execute();

                Map<String, Object> res = new HashMap<>();
                res.put("po_resultado", cs.getInt(4));
                res.put("po_mensaje", cs.getString(5));

                return res;

            });

            Integer poResultado = (Integer) resultado.get("po_resultado");
            String poMensaje = (String) resultado.get("po_mensaje");

            log.info("Procedimiento ejecutado - Resultado: {}, Mensaje: {}", poResultado, poMensaje);
            log.debug("[END] Registrar acceso PC - Éxito");


        } catch (Exception e) {
            log.error("Error al ejecutar sp_registrar_acceso_pc en esquema: {}", piEsquema, e);
            resultado.put("po_resultado", -1);
            resultado.put("po_mensaje", "Error: " + e.getMessage());
        }
        return resultado;
    }

}
