package pe.gob.onpe.scebackend.model.mapper;

import org.mapstruct.*;
import pe.gob.onpe.scebackend.model.dto.request.ConfiguracionProcesoElectoralRequestDTO;
import pe.gob.onpe.scebackend.model.dto.response.ConfiguracionProcesoElectoralResponseDTO;
import pe.gob.onpe.scebackend.model.entities.ConfiguracionProcesoElectoral;

import java.util.Date;
import java.util.Objects;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IConfiguracionProcesoElectoralMapper {

    ConfiguracionProcesoElectoralResponseDTO configuracionProcesoElectoralToDTO(ConfiguracionProcesoElectoral configuracionProcesoElectoral);

    @Mapping(target = "usuarioCreacion", source = "configuracionProcesoElectoralRequestDTO.usuario")
    ConfiguracionProcesoElectoral dtoToProceso(ConfiguracionProcesoElectoralRequestDTO configuracionProcesoElectoralRequestDTO);

    @AfterMapping
    default void doAfterMapping(@MappingTarget final ConfiguracionProcesoElectoral configuracionProcesoElectoral, final ConfiguracionProcesoElectoralRequestDTO configuracionProcesoElectoralRequestDTO) {

        configuracionProcesoElectoral.setFechaCreacion(new Date());
        if (Objects.nonNull(configuracionProcesoElectoral.getId())) {
            configuracionProcesoElectoral.setUsuarioModificacion(configuracionProcesoElectoralRequestDTO.getUsuario());
            configuracionProcesoElectoral.setFechaModificacion(new Date());
        }
    }

}
