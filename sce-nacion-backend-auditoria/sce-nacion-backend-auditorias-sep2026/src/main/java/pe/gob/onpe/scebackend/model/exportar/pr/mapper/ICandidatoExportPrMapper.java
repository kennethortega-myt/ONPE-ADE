package pe.gob.onpe.scebackend.model.exportar.pr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;


import pe.gob.onpe.scebackend.model.exportar.orc.dto.CandidatoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IFechaMapper;
import pe.gob.onpe.scebackend.model.orc.entities.Candidato;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ICandidatoExportPrMapper extends IFechaMapper  {

	
	@Mapping(target = "idDistritoElectoral", source = "candidato.distritoElectoral.id")
	@Mapping(target = "idEleccion", source = "candidato.eleccion.id")
	@Mapping(target = "codigoEleccion", source = "candidato.eleccion.codigo")
	@Mapping(target = "idAgrupacionPolitica", source = "candidato.agrupacionPolitica.id")
	@Mapping(target = "idUbigeo", source = "candidato.ubigeo.id")
	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	CandidatoExportDto toDto(Candidato candidato);
}
