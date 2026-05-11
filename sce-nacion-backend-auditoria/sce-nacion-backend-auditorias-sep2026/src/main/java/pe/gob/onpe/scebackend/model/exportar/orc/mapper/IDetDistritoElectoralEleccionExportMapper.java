package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetDistritoElectoralEleccionExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.DetDistritoElectoralEleccion;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IDetDistritoElectoralEleccionExportMapper extends IMigracionMapper<DetDistritoElectoralEleccionExportDto, DetDistritoElectoralEleccion>, IFechaMapper {

	@Mapping(target = "idEleccion", source = "detDistritoElectoralEleccion.eleccion.id") 
	@Mapping(target = "idDistritoElectoral", source = "detDistritoElectoralEleccion.distritoElectoral.id")
	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	DetDistritoElectoralEleccionExportDto toDto(DetDistritoElectoralEleccion detDistritoElectoralEleccion);
	
}
