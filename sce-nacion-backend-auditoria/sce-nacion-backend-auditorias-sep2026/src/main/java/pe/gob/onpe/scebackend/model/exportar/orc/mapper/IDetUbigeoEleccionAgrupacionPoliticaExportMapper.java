package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetUbigeoEleccionAgrupacionPoliticaExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.DetUbigeoEleccionAgrupacionPolitica;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IDetUbigeoEleccionAgrupacionPoliticaExportMapper extends IMigracionMapper<DetUbigeoEleccionAgrupacionPoliticaExportDto, DetUbigeoEleccionAgrupacionPolitica>, IFechaMapper {

	@Mapping(target = "idAgrupacionPolitica", source = "entity.agrupacionPolitica.id")
	@Mapping(target = "idDetUbigeoEleccion",  source = "entity.ubigeoEleccion.id")
	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	DetUbigeoEleccionAgrupacionPoliticaExportDto toDto(DetUbigeoEleccionAgrupacionPolitica entity);
	
}
