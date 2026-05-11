package pe.gob.onpe.sceorcbackend.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import pe.gob.onpe.sceorcbackend.model.dto.response.ProcesoElectoralResponseDTO;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ConfiguracionProcesoElectoralResponseDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ProcesoElectoral;

import java.util.List;

@Mapper(componentModel = "spring",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface IProcesoElectoralMapper {

  @Mapping(target = "CNombre", source = "nombre")
  @Mapping(target = "CAcronimo", source = "acronimo")
  @Mapping(target = "DFechaConvocatoria", source = "fechaConvocatoria")
  @Mapping(target = "NTipoAmbitoElectoral", source = "tipoAmbitoElectoral")
  @Mapping(target = "NActivo", source = "activo")
  ProcesoElectoralResponseDTO entityToDTO(ProcesoElectoral procesoElectoral);

  @Mapping(target = "nombre", source = "CNombre")
  @Mapping(target = "acronimo", source = "CAcronimo")
  @Mapping(target = "fechaConvocatoria", source = "DFechaConvocatoria")
  @Mapping(target = "tipoAmbitoElectoral", source = "NTipoAmbitoElectoral")
  @Mapping(target = "activo", source = "NActivo")
  ProcesoElectoral dtoToEntity(ProcesoElectoralResponseDTO procesoElectoralResponseDTO);


  @Mapping(target = "id", source = "id")
  @Mapping(target = "nombre", source = "CNombre")
  @Mapping(target = "acronimo", source = "CAcronimo")
  @Mapping(target = "fechaConvocatoria", source = "DFechaConvocatoria")
  @Mapping(target = "activo", source = "NActivo")
  ConfiguracionProcesoElectoralResponseDTO mapToConfiguracionProcesoElectoralResponseDTO(ProcesoElectoralResponseDTO procesoElectoralResponseDTOList);

  default List<ConfiguracionProcesoElectoralResponseDTO> mapToConfiguracionProcesoElectoralResponseDTOList(List<ProcesoElectoralResponseDTO> procesoElectoralResponseDTOList) {
    return procesoElectoralResponseDTOList.stream()
            .map(procesoElectoralResponseDTO ->
                    mapToConfiguracionProcesoElectoralResponseDTO(procesoElectoralResponseDTO)
            ).toList();
  }

}
