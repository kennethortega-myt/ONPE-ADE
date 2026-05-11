package pe.gob.onpe.sceorcbackend.model.mapper;

import pe.gob.onpe.sceorcbackend.model.dto.response.elecciones.EleccionResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabResolucion;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.resolucion.TabResolucionDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IResolucionMapper {

  TabResolucionDTO entityToDTO(TabResolucion tabResolucion);

  TabResolucion dtoToEntity(TabResolucionDTO responseDto);

}
