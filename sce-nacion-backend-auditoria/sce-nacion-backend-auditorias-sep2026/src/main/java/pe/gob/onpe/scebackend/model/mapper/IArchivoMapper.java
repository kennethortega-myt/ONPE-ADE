package pe.gob.onpe.scebackend.model.mapper;

import org.mapstruct.*;

import pe.gob.onpe.scebackend.model.dto.ArchivoDTO;
import pe.gob.onpe.scebackend.model.entities.Archivo;
import pe.gob.onpe.scebackend.utils.SceUtils;

import java.util.Date;
import java.util.Objects;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IArchivoMapper {

    ArchivoDTO archivoToDTO(Archivo archivo);

    @Mapping(target = "usuarioCreacion", source = "archivoDTO.usuario")
    Archivo dtoToArchivo(ArchivoDTO archivoDTO);
    

    @AfterMapping
    default void doAfterMapping(@MappingTarget final Archivo archivo, final ArchivoDTO archivoDTO){
        if(Objects.nonNull(archivoDTO.getId())){
            archivo.setUsuarioModificacion(archivoDTO.getUsuario());
            archivo.setFechaModificacion(new Date());
        }else{
            archivo.setGuid(SceUtils.generarGUID());
        }
    }
}
