package pe.gob.onpe.scebackend.model.exportar.pr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrEleccionExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.VwPrEleccion;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IVwPrEleccionExportPrMapper {

	VwPrEleccionExportDto toDto(VwPrEleccion vwPrEleccion);
	
}
