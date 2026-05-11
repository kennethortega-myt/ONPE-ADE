package pe.gob.onpe.scebackend.model.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteAvanceDigitalizacionJasperDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteAvanceDigitalizacionLeDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReporteAvanceDigitalizacionMapper {

    @Mapping(target = "numeMesa", source = "mesa")
    @Mapping(target = "descDepartamento", source = "departamento")
    @Mapping(target = "descProvincia", source = "provincia")
    @Mapping(target = "descDistrito", source = "distrito")
    @Mapping(target = "codCCompu", source = "codigoCentroComputo")
    @Mapping(target = "codiDesCompu", source = "nombreCentroComputo")
    @Mapping(target = "estadoDigital", source = "estadoDigitalizacion")
    @Mapping(target = "codiUbigeo", source = "codigoUbigeo")
    ReporteAvanceDigitalizacionJasperDto toReporteAvanceDigitalizacionJasperDto(ReporteAvanceDigitalizacionLeDto reporteAvanceDigitalizacionLeDto);

    List<ReporteAvanceDigitalizacionJasperDto> toReporteAvanceDigitalizacionJasperDtoList(List<ReporteAvanceDigitalizacionLeDto> reporteAvanceDigitalizacionLeDtoList);

}
