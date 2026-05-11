package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import java.util.List;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaTransmisionNacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.TransmisionReqDto;

public interface ActaTransmisionMapperService {

	List<TransmisionReqDto> mapperRequest(List<ActaTransmisionNacion> actasTransmistidas);
	
}
