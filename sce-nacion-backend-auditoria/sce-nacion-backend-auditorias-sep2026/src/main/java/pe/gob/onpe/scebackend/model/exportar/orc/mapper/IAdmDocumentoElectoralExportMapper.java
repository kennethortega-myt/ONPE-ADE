package pe.gob.onpe.scebackend.model.exportar.orc.mapper;


import org.mapstruct.*;

import pe.gob.onpe.scebackend.model.entities.DocumentoElectoral;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmDocElectoralExportDto;


@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IAdmDocumentoElectoralExportMapper extends IMigracionMapper<AdmDocElectoralExportDto, DocumentoElectoral>, IFechaMapper {
    
    @Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
    @Mapping(target = "idPadre", source = "entity.documentoElectoralPadre.id")
    AdmDocElectoralExportDto toDto(DocumentoElectoral entity);

}
