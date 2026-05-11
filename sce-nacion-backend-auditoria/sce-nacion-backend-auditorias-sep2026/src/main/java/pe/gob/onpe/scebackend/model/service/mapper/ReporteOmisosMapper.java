package pe.gob.onpe.scebackend.model.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.gob.onpe.scebackend.model.dto.reportes.OmisosJasperDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteOmisosDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReporteOmisosMapper {

    @Mapping(target = "codDescCC", source = "codigoCentroComputo")
    @Mapping(target = "totalMesas", source = "totalMesas")
    @Mapping(target = "mesasOmisas", source = "totalMesasRegistradas")
    @Mapping(target = "votantes", source = "totalElectores")
    @Mapping(target = "omisos", source = "totalOmisos")
    @Mapping(target = "codUbigeo", source = "codigoUbigeo", defaultValue = "")
    @Mapping(target = "descDepartamento", source = "departamento", defaultValue = "")
    @Mapping(target = "descProvincia", source = "provincia", defaultValue = "")
    @Mapping(target = "descDistrito", source = "distrito", defaultValue = "")
    OmisosJasperDto toOmisosJasperDto(ReporteOmisosDto reporteOmisosDto);

    List<OmisosJasperDto> toOmisosJasperDtoList(List<ReporteOmisosDto> reporteOmisosDtoList);

}