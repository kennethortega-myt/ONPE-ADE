package pe.gob.onpe.scebackend.model.mapper;

import org.mapstruct.*;

import pe.gob.onpe.scebackend.model.dto.request.DetalleTipoEleccionDocumentoElectoralRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.DetalleTipoEleccionDocumentoElectoralResponseDto;
import pe.gob.onpe.scebackend.model.entities.DetalleTipoEleccionDocumentoElectoral;

import java.util.Date;
import java.util.Objects;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IDetalleTipoProcesoDocumentoElectoralMapper {

    DetalleTipoEleccionDocumentoElectoralResponseDto detalleTipoProcesoElectoralToDTO(DetalleTipoEleccionDocumentoElectoral detalleTipoEleccionDocumentoElectoral);

    DetalleTipoEleccionDocumentoElectoral dtoToDetalleTipoProcesoElectoral(DetalleTipoEleccionDocumentoElectoralRequestDto detalleTipoEleccionDocumentoElectoralRequestDto);
    
    @BeforeMapping
    default void doAfterMapping(@MappingTarget final DetalleTipoEleccionDocumentoElectoral detalleTipoEleccionDocumentoElectoral, final DetalleTipoEleccionDocumentoElectoralRequestDto detalleTipoEleccionDocumentoElectoralRequestDto){
        if(Objects.nonNull(detalleTipoEleccionDocumentoElectoralRequestDto.getId())){
            detalleTipoEleccionDocumentoElectoral.setUsuarioModificacion("JCISNERP");
            detalleTipoEleccionDocumentoElectoral.setFechaModificacion(new Date());
            detalleTipoEleccionDocumentoElectoral.setUsuarioCreacion("JCISNERP");
            detalleTipoEleccionDocumentoElectoral.setFechaCreacion(new Date());
        }else{
            detalleTipoEleccionDocumentoElectoral.setUsuarioCreacion("JCISNERP");
            detalleTipoEleccionDocumentoElectoral.setFechaCreacion(new Date());
        }
    }
}
