package pe.gob.onpe.scebatchpr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebatchpr.dto.VwPrActaExportDto;
import pe.gob.onpe.scebatchpr.entities.orc.VwPrActa;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IVwPrActaExportPrMapper {

	VwPrActaExportDto toDto(VwPrActa vwPrActa);
	
}
