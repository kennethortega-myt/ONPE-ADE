package pe.gob.onpe.sceorcbackend.model.postgresql.mapper.transmision;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.TransmisionDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.TransmisionReqDto;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ITransmisionMapper {

	TransmisionReqDto toDto(TransmisionDto transmisionDto);
	
}
