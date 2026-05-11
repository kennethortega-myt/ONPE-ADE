package pe.gob.onpe.scebackend.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import pe.gob.onpe.scebackend.model.dto.DetParametroDto;
import pe.gob.onpe.scebackend.model.orc.entities.DetParametro;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IDetParametroMapper {

    DetParametroDto entityToDTO(DetParametro entity);

    DetParametro dtoToEntity(DetParametroDto dto);

}