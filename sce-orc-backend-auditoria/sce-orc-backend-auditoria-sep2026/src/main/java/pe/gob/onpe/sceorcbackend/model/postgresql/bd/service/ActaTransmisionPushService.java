package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;



public interface ActaTransmisionPushService {

	boolean empujar(Long idActa, String proceso, String usr);
	
	boolean resetear(Long idActa, String proceso, String usr);
	
	boolean verificarTransmisionesEjecucion(Long idActa);
	
}
