package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DistritoElectoralExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.DistritoElectoral;


@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IDistritoElectoralExportMapper extends IMigracionMapper<DistritoElectoralExportDto, DistritoElectoral>, IFechaMapper {

	
	@Mapping(target = "idDistritoElectoralPadre", source = "distritoElectoral.distritoElectoralPadre.id")
	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	DistritoElectoralExportDto toDto(DistritoElectoral distritoElectoral);
	
}
