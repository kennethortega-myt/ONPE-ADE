package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;


import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetActaPreferencialExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.DetActaPreferencial;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IDetActaPreferencialExportMapper extends IMigracionMapper<DetActaPreferencialExportDto, DetActaPreferencial>, IFechaMapper {

	@Mapping(target = "detActaId", source = "detActaPreferencial.detActa.id")
	@Mapping(target = "distritoElectoralId", source = "detActaPreferencial.distritoElectoral.id")
	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	DetActaPreferencialExportDto toDto(DetActaPreferencial detActaPreferencial);
	
	
}
