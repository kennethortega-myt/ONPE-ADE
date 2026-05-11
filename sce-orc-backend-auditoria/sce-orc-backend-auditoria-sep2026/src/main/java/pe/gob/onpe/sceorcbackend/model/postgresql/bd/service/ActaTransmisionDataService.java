package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import java.util.List;
import java.util.Optional;

import pe.gob.onpe.sceorcbackend.model.dto.queue.EnvioTransmisionQueue;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaTransmisionNacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.TransmisionReqDto;

public interface ActaTransmisionDataService {

	
	void ejecutandose(Long idTransmision);
	void actualizarFallido(Long idTransmision, String mensaje);
	void actualizarOk(Long idTransmision, String mensaje);
	void actualizarFallidoReseteo(Long idActa, String mensaje);
	void actualizarOkReseteo(Long idActa, String mensaje);
	Optional<ActaTransmisionNacion> findByIdActaTransmiion(Long idActaTransmision);
	void actualizarEstado(Long idTransmision, Integer estado, String mensaje);
	List<TransmisionReqDto> adjuntar(List<TransmisionReqDto> actasTransmistidas);
	List<ActaTransmisionNacion> findByIdActaConTransmisionesOrdenadas(Long idActa);
	List<ActaTransmisionNacion> findByIdActaTodasTransmisionesOrdenadas(Long idActa);
	
	EnvioTransmisionQueue guardarTransmisionAndSend(
			Long idActa, 
			TransmisionNacionEnum estadoEnum, 
			String usuario, 
			String proceso);
	
	Integer contarEnEjecucion(Long idActa);
	
	void guardarTransmisionStae(Long idActa, TransmisionNacionEnum estadoEnum, String usuario, String proceso);
	void actualizarEstadoReseteo(Long idActa, Integer estado, String mensaje);
	

	
}
