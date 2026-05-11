package pe.gob.onpe.scebackend.model.mapper;


import org.mapstruct.*;

import pe.gob.onpe.scebackend.model.dto.request.DatosGeneralesRequestDto;
import pe.gob.onpe.scebackend.model.dto.request.DocumentoElectoralRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.DocumentoElectoralResponseDto;
import pe.gob.onpe.scebackend.model.entities.DocumentoElectoral;

import java.util.Date;
import java.util.Objects;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IDocumentoElectoralMapper {

    DocumentoElectoralResponseDto documentoToDTO(DocumentoElectoral documentoElectoral);
    
    @Mapping(target = "usuarioCreacion", source = "datosGeneralesRequestDto.usuario")
    DocumentoElectoral dtoToDocumento(DocumentoElectoralRequestDto datosGeneralesRequestDto);


    @AfterMapping
    default void doAfterMapping(@MappingTarget final DocumentoElectoral documentoElectoral, final DatosGeneralesRequestDto datosGeneralesRequestDto){
        if(Objects.nonNull(datosGeneralesRequestDto.getId())){
            documentoElectoral.setUsuarioModificacion(datosGeneralesRequestDto.getUsuario());
            documentoElectoral.setFechaModificacion(new Date());
        }
    }
}
