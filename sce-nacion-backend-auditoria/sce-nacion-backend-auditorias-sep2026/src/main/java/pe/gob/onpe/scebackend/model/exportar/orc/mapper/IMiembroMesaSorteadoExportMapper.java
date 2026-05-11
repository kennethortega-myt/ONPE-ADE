package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.MiembroMesaSorteadoExportDto;
import pe.gob.onpe.scebackend.model.orc.entities.MiembroMesaSorteado;



@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IMiembroMesaSorteadoExportMapper extends IMigracionMapper<MiembroMesaSorteadoExportDto, MiembroMesaSorteado>, IFechaMapper {

	@Mapping(target = "idMesa", source = "miembroMesaSorteado.mesa.id")
	@Mapping(target = "idPadronElectoral", source = "miembroMesaSorteado.padronElectoral.id")
	@Mapping(target = "audUsuarioCreacion", source = "usuarioCreacion")
	@Mapping(target = "audUsuarioModificacion", source = "usuarioModificacion")
	@Mapping(target = "audFechaCreacion", source = "fechaCreacion", qualifiedByName = "convertirFecha")
	@Mapping(target = "audFechaModificacion", source = "fechaModificacion", qualifiedByName = "convertirFecha")
	MiembroMesaSorteadoExportDto toDto(MiembroMesaSorteado miembroMesaSorteado);
}
