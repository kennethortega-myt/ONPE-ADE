package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import java.util.List;

import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;

public interface ActaTransmisionExecuteService {


	void sincronizar(Long idActa, String proceso, TransmisionNacionEnum estadoEnum, String usuario);
	void sincronizar(List<Long> idActas, String proceso, TransmisionNacionEnum estadoEnum, String usuario);

	
}
