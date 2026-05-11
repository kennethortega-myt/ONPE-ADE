package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;


import pe.gob.onpe.scebackend.model.exportar.orc.dto.TabJuradoElectoralEspecialExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.JuradoElectoralEspecial;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ITabJuradoElectoralEspecialExportMapper 
	extends IMigracionMapper<TabJuradoElectoralEspecialExportDto, JuradoElectoralEspecial>, IFechaMapper {
	
	@Mapping(target = "fechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "fechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	TabJuradoElectoralEspecialExportDto toDto(JuradoElectoralEspecial juradoElectoralEspecial);
	

}
