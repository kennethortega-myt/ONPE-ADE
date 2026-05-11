package pe.gob.onpe.scebackend.model.mapper;

import org.mapstruct.*;

import pe.gob.onpe.scebackend.model.dto.request.DatosGeneralesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.DatosGeneralesResponseDto;
import pe.gob.onpe.scebackend.model.entities.TipoEleccion;

import java.util.Date;
import java.util.Objects;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ITipoEleccionMapper  {

    DatosGeneralesResponseDto tipoEleccionToDTO(TipoEleccion tipoEleccion);

    @Mapping(target = "usuarioCreacion", source = "datosGeneralesRequestDto.usuario")
    TipoEleccion dtoToTipoEleccion(DatosGeneralesRequestDto datosGeneralesRequestDto);
    
    @AfterMapping
    default void doAfterMapping(@MappingTarget final TipoEleccion tipoEleccion, final DatosGeneralesRequestDto datosGeneralesRequestDto){
        if(Objects.nonNull(datosGeneralesRequestDto.getId())){
            tipoEleccion.setUsuarioModificacion(datosGeneralesRequestDto.getUsuario());
            tipoEleccion.setFechaModificacion(new Date());
        }
    }
}
