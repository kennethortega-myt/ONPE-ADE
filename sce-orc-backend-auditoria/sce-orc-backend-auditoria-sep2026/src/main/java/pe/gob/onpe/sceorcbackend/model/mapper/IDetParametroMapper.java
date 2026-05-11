package pe.gob.onpe.sceorcbackend.model.mapper;

import pe.gob.onpe.sceorcbackend.model.dto.DetParametroDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetParametro;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IDetParametroMapper {

  DetParametroDto entityToDTO(DetParametro entity);

  DetParametro dtoToEntity(DetParametroDto dto);

}
