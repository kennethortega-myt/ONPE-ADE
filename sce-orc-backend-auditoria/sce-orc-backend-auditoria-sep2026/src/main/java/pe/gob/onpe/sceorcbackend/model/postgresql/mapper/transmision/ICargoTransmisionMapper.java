package pe.gob.onpe.sceorcbackend.model.postgresql.mapper.transmision;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmisioncargo.json.TransmisionCargoDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmisioncargo.TransmisionCargoReqDto;

@Mapper(componentModel = "spring",
	nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, 
	nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ICargoTransmisionMapper {

	TransmisionCargoReqDto toDto(TransmisionCargoDto transmisionDto);
	
}
