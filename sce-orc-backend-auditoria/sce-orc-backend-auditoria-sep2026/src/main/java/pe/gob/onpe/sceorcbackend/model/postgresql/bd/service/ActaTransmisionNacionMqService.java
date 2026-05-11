package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;

public interface ActaTransmisionNacionMqService {

	void sincronizar(Long idActa, String proceso, TransmisionNacionEnum estadoEnum, String usuario);
	void tramsmitirActa(Long idActa, String proceso, String usuario);
	void procesarReintentos();
	
}
