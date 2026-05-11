package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import net.sf.jasperreports.engine.JRException;

public interface VerificaVersionService {

    void puestaCero(String usuario);

    String procesarOrc(String usuario, String centroComputo) throws Exception;

    String procesarOrc(String usuario, String centroComputo, String abrevProceso) throws Exception;

    byte[] reporteVerificaVersion(String acronimoProceso, String centroComputo, String ncc, String usuario) throws JRException;

}
