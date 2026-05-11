package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.dto.AmbitoElectoralDto;
import pe.gob.onpe.scebackend.model.orc.entities.AmbitoElectoral;


@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IAmbitoElectoralExportMapper extends IMigracionMapper<AmbitoElectoralDto, AmbitoElectoral>, IFechaMapper {

	@Mapping(target = "idPadre", source = "ambitoElectoral.ambitoElectoralPadre.id")
	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	AmbitoElectoralDto toDto(AmbitoElectoral ambitoElectoral);
	
	
	
}
