package pe.gob.onpe.scebatchpr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebatchpr.dto.VwPrMesaExportDto;
import pe.gob.onpe.scebatchpr.entities.orc.VwPrMesa;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IVwPrMesaExportPrMapper {

	VwPrMesaExportDto toDto(VwPrMesa vwPrMesa);
	
}
