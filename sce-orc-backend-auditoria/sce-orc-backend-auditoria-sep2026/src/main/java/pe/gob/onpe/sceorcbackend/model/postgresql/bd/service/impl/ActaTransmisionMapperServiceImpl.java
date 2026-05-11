package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaTransmisionNacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionMapperService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.TransmisionReqDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.mapper.transmision.ITransmisionMapper;

@Service
@AllArgsConstructor
public class ActaTransmisionMapperServiceImpl implements ActaTransmisionMapperService {

	private final ITransmisionMapper mapper;
	
	@Override
	@Transactional(readOnly = true)
    public List<TransmisionReqDto> mapperRequest(List<ActaTransmisionNacion> actasTransmistidas){
		return actasTransmistidas
		.stream()
		.map(t -> this.mapper.toDto(t.getRequestActaTransmision()))
		.toList();
	}
	
}
