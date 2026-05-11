package pe.gob.onpe.sceorcbackend.model.mapper;

import pe.gob.onpe.sceorcbackend.model.dto.response.ProcesoElectoralResponseDTO;
import pe.gob.onpe.sceorcbackend.model.dto.response.elecciones.EleccionResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ConfiguracionProcesoElectoralResponseDTO;
import pe.gob.onpe.sceorcbackend.model.importar.dto.EleccionDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IEleccionMapper {

  @Mapping(target = "CNombre", source = "nombre")
  @Mapping(target = "CCodigo", source = "codigo")
  @Mapping(target = "NActivo", source = "activo")
  EleccionResponseDto entityToDTO(Eleccion eleccion);

  @Mapping(target = "nombre", source = "CNombre")
  @Mapping(target = "codigo", source = "CCodigo")
  @Mapping(target = "activo", source = "NActivo")
  Eleccion dtoToEntity(EleccionResponseDto responseDto);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "nombre", source = "CNombre")
  @Mapping(target = "codigo", source = "CCodigo")
  @Mapping(target = "activo", source = "NActivo")
  EleccionDto mapToEleccionDto(EleccionResponseDto procesoElectoralResponseDTOList);

  default List<EleccionDto> mapToEleccionDtoList(List<EleccionResponseDto> procesoElectoralResponseDTOList) {
    return procesoElectoralResponseDTOList.stream()
            .map(procesoElectoralResponseDTO ->
                    mapToEleccionDto(procesoElectoralResponseDTO)
            ).toList();
  }

}
