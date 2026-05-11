package pe.gob.onpe.scebackend.ext.pr.service;


import pe.gob.onpe.scebackend.ext.pr.dto.RegistroTramaParam;

public interface EnvioTramaSceService {

	void generarRegistrosTransmisionPr(RegistroTramaParam params);
	void generarRegistrosTransmisionJne(RegistroTramaParam params);
	
	
}
