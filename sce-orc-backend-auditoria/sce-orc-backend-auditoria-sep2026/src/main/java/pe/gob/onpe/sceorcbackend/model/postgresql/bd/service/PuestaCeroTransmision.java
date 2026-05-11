package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import java.util.Date;

public interface PuestaCeroTransmision {

	void sincronizar(String proceso, String cc, String usuario, Date fechaEjecucion, boolean transmitir);
}
