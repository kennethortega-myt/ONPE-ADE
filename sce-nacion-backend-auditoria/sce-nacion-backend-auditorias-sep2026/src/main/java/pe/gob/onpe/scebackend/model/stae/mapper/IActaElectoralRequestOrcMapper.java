package pe.gob.onpe.scebackend.model.stae.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.stae.dto.ActaElectoralRequestDto;
import pe.gob.onpe.scebackend.model.stae.dto.ActaElectoralRequestOrcDto;


@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IActaElectoralRequestOrcMapper {

	ActaElectoralRequestOrcDto toDto(ActaElectoralRequestDto request);
	
}
