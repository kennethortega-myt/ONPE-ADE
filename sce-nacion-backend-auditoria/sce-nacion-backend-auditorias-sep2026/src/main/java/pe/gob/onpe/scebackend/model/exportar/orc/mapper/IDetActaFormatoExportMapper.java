package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetActaFormatoExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.DetActaFormato;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IDetActaFormatoExportMapper extends IMigracionMapper<DetActaFormatoExportDto, DetActaFormato>, IFechaMapper {

	@Mapping(target = "idActa", source = "detActaFormato.acta.id")
	@Mapping(target = "idCabActaFormato", source = "cabActaFormato.id")
	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	DetActaFormatoExportDto toDto(DetActaFormato detActaFormato);

}
