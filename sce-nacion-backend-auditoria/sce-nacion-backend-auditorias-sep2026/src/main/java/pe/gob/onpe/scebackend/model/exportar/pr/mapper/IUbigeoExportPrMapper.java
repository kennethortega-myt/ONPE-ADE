package pe.gob.onpe.scebackend.model.exportar.pr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.UbigeoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IFechaMapper;
import pe.gob.onpe.scebackend.model.orc.entities.Ubigeo;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IUbigeoExportPrMapper extends IFechaMapper {

	@Mapping(target = "idCentroComputo", source = "ubigeo.centroComputo.id")
	@Mapping(target = "idAmbitoElectoral", source = "ubigeo.ambitoElectoral.id")
	@Mapping(target = "idPadre", source = "ubigeo.ubigeoPadre.id")
	@Mapping(target = "distrito", source = "ubigeo.nombre")
	@Mapping(target = "provincia", source = "ubigeo.ubigeoPadre.nombre")
	@Mapping(target = "departamento", source = "ubigeo.ubigeoPadre.ubigeoPadre.nombre")
	@Mapping(target = "idDistritoElectoral", source = "ubigeo.distritoElectoral.id")
	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	UbigeoExportDto toDto(Ubigeo ubigeo);
	
}
