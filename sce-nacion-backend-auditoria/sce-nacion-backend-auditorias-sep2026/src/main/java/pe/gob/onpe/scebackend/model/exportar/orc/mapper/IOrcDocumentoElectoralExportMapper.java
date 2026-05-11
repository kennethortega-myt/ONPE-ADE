package pe.gob.onpe.scebackend.model.exportar.orc.mapper;


import org.mapstruct.*;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.OrcDocElectoralExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.OrcDocumentoElectoral;


@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IOrcDocumentoElectoralExportMapper extends IMigracionMapper<OrcDocElectoralExportDto, OrcDocumentoElectoral>, IFechaMapper {
    
    @Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
    @Mapping(target = "idPadre", source = "entity.documentoElectoralPadre.id")
    OrcDocElectoralExportDto toDto(OrcDocumentoElectoral entity);

}
