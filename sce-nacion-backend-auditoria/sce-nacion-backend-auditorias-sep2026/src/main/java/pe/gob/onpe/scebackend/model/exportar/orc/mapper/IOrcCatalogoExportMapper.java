package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.OrcCatalogoExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.OrcCatalogo;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IOrcCatalogoExportMapper extends IFechaMapper {

	@Mapping(target = "idPadre", source = "catalogo.catalogoPadre.id")
	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	OrcCatalogoExportDto toDto(OrcCatalogo catalogo);
	
}
