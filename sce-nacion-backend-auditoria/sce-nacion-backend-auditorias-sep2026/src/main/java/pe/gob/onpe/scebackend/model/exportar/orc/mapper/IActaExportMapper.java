package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CabActaExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.Acta;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IActaExportMapper extends IMigracionMapper<CabActaExportDto, Acta>, IFechaMapper {

	@Mapping(target = "idArchivoEscrutinio", source = "acta.archivoEscrutinio.id")
	@Mapping(target = "idArchivoInstalacionSufragio", source = "acta.archivoInstalacionSufragio.id")
	@Mapping(target = "idDetUbigeoEleccion", source = "acta.ubigeoEleccion.id") 
	@Mapping(target = "idMesa", source = "acta.mesa.id")
	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	CabActaExportDto toDto(Acta acta);
	
}
