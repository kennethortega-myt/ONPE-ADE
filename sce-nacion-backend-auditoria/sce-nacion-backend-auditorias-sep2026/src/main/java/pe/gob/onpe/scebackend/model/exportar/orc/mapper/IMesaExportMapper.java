package pe.gob.onpe.scebackend.model.exportar.orc.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.MesaExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.Mesa;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IMesaExportMapper extends IMigracionMapper<MesaExportDto, Mesa>, IFechaMapper {

	@Mapping(target = "idLocalVotacion", source = "mesa.localVotacion.id")
	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	MesaExportDto toDto(Mesa mesa);
	
}
