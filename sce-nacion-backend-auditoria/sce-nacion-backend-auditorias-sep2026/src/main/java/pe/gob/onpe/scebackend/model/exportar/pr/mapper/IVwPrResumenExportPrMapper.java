package pe.gob.onpe.scebackend.model.exportar.pr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrResumenExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.VwPrResumen;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IVwPrResumenExportPrMapper {

	VwPrResumenExportDto toDto(VwPrResumen vwPrResumen);
	
}
