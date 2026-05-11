package pe.gob.onpe.scebatchpr.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebatchpr.dto.VwPrEleccionExportDto;
import pe.gob.onpe.scebatchpr.entities.orc.VwPrEleccion;



@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IVwPrEleccionExportPrMapper {

	VwPrEleccionExportDto toDto(VwPrEleccion vwPrEleccion);
	
}
