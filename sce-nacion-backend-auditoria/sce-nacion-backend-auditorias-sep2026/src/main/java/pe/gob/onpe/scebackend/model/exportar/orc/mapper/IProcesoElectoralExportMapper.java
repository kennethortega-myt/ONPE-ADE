package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.ProcesoElectoralExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.ProcesoElectoral;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IProcesoElectoralExportMapper extends IMigracionMapper<ProcesoElectoralExportDto, ProcesoElectoral>, IFechaMapper {

	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "fechaConvocatoria", source = "fechaConvocatoria", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	ProcesoElectoralExportDto toDto(ProcesoElectoral procesoElectoral);
	
}
