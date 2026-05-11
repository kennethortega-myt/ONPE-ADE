package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.*;

import pe.gob.onpe.scebackend.model.entities.TipoEleccion;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmTipoEleccionExportDto;


@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ITipoEleccionExportMapper extends IMigracionMapper<AdmTipoEleccionExportDto, TipoEleccion>, IFechaMapper {

    @Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
    AdmTipoEleccionExportDto toDto(TipoEleccion entity);

}
