package pe.gob.onpe.scebackend.model.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.gob.onpe.scebackend.model.dto.reportes.ReporteMesasSinOmisosJasperDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteMesasSinOmisosDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReporteMesasSinOmisosMapper {

    @Mapping(target = "codUbigeo", source = "codigoUbigeo", defaultValue = "")
    @Mapping(target = "codDescCC", source = "codigoCentroComputo", defaultValue = "")
    @Mapping(target = "codDescODPE", source = "codigoODPE", defaultValue = "")
    @Mapping(target = "descDepartamento", source = "departamento", defaultValue = "")
    @Mapping(target = "descProvincia", source = "provincia", defaultValue = "")
    @Mapping(target = "descDistrito", source = "distrito", defaultValue = "")
    @Mapping(target = "numMesa", source = "numeroMesa", defaultValue = "")
    ReporteMesasSinOmisosJasperDto toMesasSinOmisosJasperDto(ReporteMesasSinOmisosDto reporteMesasSinOmisosDto);

    List<ReporteMesasSinOmisosJasperDto> toMesasSinOmisosJasperDtoList(List<ReporteMesasSinOmisosDto> reporteMesasSinOmisosDtoList);

}