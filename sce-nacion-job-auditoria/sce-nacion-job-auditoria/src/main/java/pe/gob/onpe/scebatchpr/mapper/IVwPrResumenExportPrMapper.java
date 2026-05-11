package pe.gob.onpe.scebatchpr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebatchpr.dto.VwPrResumenExportDto;
import pe.gob.onpe.scebatchpr.entities.orc.VwPrResumen;


@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IVwPrResumenExportPrMapper {

	VwPrResumenExportDto toDto(VwPrResumen vwPrResumen);
	
}
