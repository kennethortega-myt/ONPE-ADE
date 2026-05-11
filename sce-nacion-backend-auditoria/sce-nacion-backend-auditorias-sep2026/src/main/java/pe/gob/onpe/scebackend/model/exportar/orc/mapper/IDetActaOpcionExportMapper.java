package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetActaOpcionExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.DetActaOpcion;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IDetActaOpcionExportMapper 
		extends IMigracionMapper<DetActaOpcionExportDto, DetActaOpcion>, IFechaMapper {

	@Mapping(target = "idDetActa", source = "detActaOpcion.detActa.id")
	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	DetActaOpcionExportDto toDto(DetActaOpcion detActaOpcion);
	
}
