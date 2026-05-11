package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.*;

import pe.gob.onpe.scebackend.model.entities.ConfiguracionProcesoElectoral;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmConfigProcesoElectoralExportDto;


@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IAdmConfiguracionProcesoExportElectoralMapper extends IMigracionMapper<AdmConfigProcesoElectoralExportDto, ConfiguracionProcesoElectoral>, IFechaMapper {

    @Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
    @Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
    @Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
    @Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
    AdmConfigProcesoElectoralExportDto toDto(ConfiguracionProcesoElectoral entity);


}
