package pe.gob.onpe.scebackend.model.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteAvanceDigitalizacionDenunciasDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteAvanceDigitalizacionJasperDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReporteAvanceDigitalizacionDenunciasMapper {

    @Mapping(target = "numeMesa", expression = "java( reporteAvanceDigitalizacionDenunciasDto.getNumeroDocumento() + \" - \" + reporteAvanceDigitalizacionDenunciasDto.getTipoPerdida())")
    @Mapping(target = "descDepartamento", source = "departamento")
    @Mapping(target = "descProvincia", source = "provincia")
    @Mapping(target = "descDistrito", source = "distrito")
    @Mapping(target = "codCCompu", source = "codigoCentroComputo")
    @Mapping(target = "codiDesCompu", source = "nombreCentroComputo")
    @Mapping(target = "estadoDigital", source = "estadoDigitalizacion")
    @Mapping(target = "codiUbigeo", source = "codigoUbigeo")
    ReporteAvanceDigitalizacionJasperDto toReporteAvanceDigitalizacionJasperDto(ReporteAvanceDigitalizacionDenunciasDto reporteAvanceDigitalizacionDenunciasDto);

    List<ReporteAvanceDigitalizacionJasperDto> toReporteAvanceDigitalizacionJasperDtoList(List<ReporteAvanceDigitalizacionDenunciasDto> reporteAvanceDigitalizacionDenunciasDtoList);

}
