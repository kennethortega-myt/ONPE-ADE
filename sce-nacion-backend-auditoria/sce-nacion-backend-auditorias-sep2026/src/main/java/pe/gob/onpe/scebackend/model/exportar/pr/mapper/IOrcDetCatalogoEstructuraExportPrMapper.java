package pe.gob.onpe.scebackend.model.exportar.pr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmDetCatalogoEstructuraExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IFechaMapper;
import pe.gob.onpe.scebackend.model.orc.entities.OrcDetalleCatalogoEstructura;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IOrcDetCatalogoEstructuraExportPrMapper extends IFechaMapper {

	@Mapping(target = "idCatalogo", source = "detalleCatalogoEstructura.catalogo.id")
	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	AdmDetCatalogoEstructuraExportDto toDto(OrcDetalleCatalogoEstructura detalleCatalogoEstructura);
	
}
