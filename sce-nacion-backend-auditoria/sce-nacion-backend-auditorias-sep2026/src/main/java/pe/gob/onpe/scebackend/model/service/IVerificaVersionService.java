package pe.gob.onpe.scebackend.model.service;

import net.sf.jasperreports.engine.JRException;

public interface IVerificaVersionService {

    Boolean puestaCero(String esquema,String usuario);
    Boolean procesar(String usuario, String esquema);

    byte[] reporteVerificaVersion(String nombrePrroceso,String acronimo, String usuario, String esquema) throws JRException;
}
