package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetParametroExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.DetParametro;

@Mapper(componentModel = "spring",
nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, 
nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IDetParametroExportMapper extends IMigracionMapper<DetParametroExportDto, DetParametro>, IFechaMapper {

	
	@Mapping(target = "idParametro", source = "detParametro.parametro.id")
	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	DetParametroExportDto toDto(DetParametro detParametro);
	
}
