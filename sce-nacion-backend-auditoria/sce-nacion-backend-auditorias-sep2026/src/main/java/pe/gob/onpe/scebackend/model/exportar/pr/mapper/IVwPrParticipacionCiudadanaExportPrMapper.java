package pe.gob.onpe.scebackend.model.exportar.pr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrParticipacionCiudadanaExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.VwPrParticipacionCiudadana;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IVwPrParticipacionCiudadanaExportPrMapper {

	VwPrParticipacionCiudadanaExportDto toDto(VwPrParticipacionCiudadana wPrParticipacionCiudadana);
	
}
