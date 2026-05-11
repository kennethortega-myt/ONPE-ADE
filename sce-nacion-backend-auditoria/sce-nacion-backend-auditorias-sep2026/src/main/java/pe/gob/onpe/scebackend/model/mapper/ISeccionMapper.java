package pe.gob.onpe.scebackend.model.mapper;

import org.mapstruct.*;

import pe.gob.onpe.scebackend.model.dto.request.DatosGeneralesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.DatosGeneralesResponseDto;
import pe.gob.onpe.scebackend.model.entities.Seccion;

import java.util.Date;
import java.util.Objects;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ISeccionMapper {
    
    DatosGeneralesResponseDto seccionToDTO(Seccion seccion);

    @Mapping(target = "usuarioCreacion", source = "datosGeneralesRequestDto.usuario")
    Seccion dtoToSeccion(DatosGeneralesRequestDto datosGeneralesRequestDto);

    @AfterMapping
    default void doAfterMapping(@MappingTarget final Seccion seccion, final DatosGeneralesRequestDto datosGeneralesRequestDto){
        if(Objects.nonNull(datosGeneralesRequestDto.getId())){
            seccion.setUsuarioModificacion(datosGeneralesRequestDto.getUsuario());
            seccion.setFechaModificacion(new Date());
        }
    }
}
