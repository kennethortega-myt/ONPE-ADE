package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.VersionModeloExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.VersionModelo;

@Mapper(componentModel = "spring",
	nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, 
	nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IVersionModeloExportMapper extends IFechaMapper {

	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "fechaVersion", source = "fechaVersion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	VersionModeloExportDto toDto(VersionModelo version);
	
}
