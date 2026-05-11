package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.LogUtil;

@Service
public class LogUtilImpl implements LogUtil {

    @Override
    public void iniciarPropiedadesLog(String mesa, Long acta) {
        if(StringUtils.isNotBlank(mesa)){
            MDC.put("mesa",mesa);
        }
        if(acta!=null){
            MDC.put("acta",acta.toString());
        }
    }

    @Override
    public void limpiarPropiedadesLog() {
        MDC.remove("mesa");
        MDC.remove("acta");
    }
}
