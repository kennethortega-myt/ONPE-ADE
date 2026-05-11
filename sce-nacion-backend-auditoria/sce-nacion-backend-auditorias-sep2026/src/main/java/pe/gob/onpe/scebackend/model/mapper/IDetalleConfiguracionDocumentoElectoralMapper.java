package pe.gob.onpe.scebackend.model.mapper;

import org.mapstruct.*;

import pe.gob.onpe.scebackend.model.dto.request.DetalleConfiguracionDocumentoElectoralRequestDTO;
import pe.gob.onpe.scebackend.model.dto.response.DetalleConfiguracionDocumentoElectoralResponseDTO;
import pe.gob.onpe.scebackend.model.entities.DetalleConfiguracionDocumentoElectoral;

import java.util.Date;
import java.util.Objects;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IDetalleConfiguracionDocumentoElectoralMapper {

    DetalleConfiguracionDocumentoElectoralResponseDTO detalleConfiguracionElectoralToDTO(DetalleConfiguracionDocumentoElectoral detalleConfiguracionDocumentoElectoral);

    @Mapping(target = "usuarioCreacion", source = "detalleConfiguracionDocumentoElectoralRequestDTO.usuario")
    DetalleConfiguracionDocumentoElectoral dtoToSeccion(DetalleConfiguracionDocumentoElectoralRequestDTO detalleConfiguracionDocumentoElectoralRequestDTO);
    
    @AfterMapping
    default void doAfterMapping(@MappingTarget final DetalleConfiguracionDocumentoElectoral detalleConfiguracionDocumentoElectoral, final DetalleConfiguracionDocumentoElectoralRequestDTO detalleConfiguracionDocumentoElectoralRequestDTO){
        detalleConfiguracionDocumentoElectoral.setFechaCreacion(new Date());
        if(Objects.nonNull(detalleConfiguracionDocumentoElectoral.getId())){
            detalleConfiguracionDocumentoElectoral.setUsuarioModificacion(detalleConfiguracionDocumentoElectoralRequestDTO.getUsuario());
            detalleConfiguracionDocumentoElectoral.setFechaModificacion(new Date());
        }
    }
}
