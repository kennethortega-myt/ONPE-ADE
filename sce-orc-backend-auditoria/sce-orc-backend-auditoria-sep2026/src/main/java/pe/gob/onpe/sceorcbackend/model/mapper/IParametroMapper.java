package pe.gob.onpe.sceorcbackend.model.mapper;

import pe.gob.onpe.sceorcbackend.model.dto.ParametroDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabParametro;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IParametroMapper {

  ParametroDto entityToDTO(CabParametro entity);

  CabParametro dtoToEntity(ParametroDto dto);

}
