package pe.gob.onpe.scebackend.model.service;

public interface TransmisionControlService {

	void actualizarOrden(String codigoCc, Long idActa , Integer ordenRecibido);
	
	Integer getOrden(String codigoCc, Long idActa); 
	
}
