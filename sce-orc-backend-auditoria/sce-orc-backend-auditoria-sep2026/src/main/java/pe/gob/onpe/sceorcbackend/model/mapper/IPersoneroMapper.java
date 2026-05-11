package pe.gob.onpe.sceorcbackend.model.mapper;

import pe.gob.onpe.sceorcbackend.model.dto.PersoneroDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Personero;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IPersoneroMapper {

  PersoneroDTO entityToDTO(Personero entity);

  Personero dtoToEntity(PersoneroDTO dto);

}
