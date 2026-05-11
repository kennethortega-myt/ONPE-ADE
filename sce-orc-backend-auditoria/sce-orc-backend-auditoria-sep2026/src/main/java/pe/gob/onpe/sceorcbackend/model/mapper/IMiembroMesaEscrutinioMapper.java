package pe.gob.onpe.sceorcbackend.model.mapper;

import pe.gob.onpe.sceorcbackend.model.dto.MiembroMesaEscrutinioDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MiembroMesaEscrutinio;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IMiembroMesaEscrutinioMapper {

  MiembroMesaEscrutinioDTO entityToDTO(MiembroMesaEscrutinio entity);

  MiembroMesaEscrutinio dtoToEntity(MiembroMesaEscrutinioDTO dto);

}
