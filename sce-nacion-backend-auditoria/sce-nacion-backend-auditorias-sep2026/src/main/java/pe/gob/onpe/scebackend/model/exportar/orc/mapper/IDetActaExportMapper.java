package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetActaExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.DetActa;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IDetActaExportMapper extends IMigracionMapper<DetActaExportDto, DetActa>, IFechaMapper {

	@Mapping(target = "idCabActa", source = "detActa.acta.id")
	@Mapping(target = "idAgrupacionPolitica", source = "detActa.agrupacionPolitica.id")
	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	DetActaExportDto toDto(DetActa detActa);
	
}
