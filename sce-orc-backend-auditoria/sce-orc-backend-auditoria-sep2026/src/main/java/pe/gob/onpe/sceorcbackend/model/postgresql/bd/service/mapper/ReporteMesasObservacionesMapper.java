package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteMesasObservacionesDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteMesasObservacionesJasperDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReporteMesasObservacionesMapper {

    @Mapping(target = "observacion", expression = "java( reporteMesasObservacionesDto.getPagina() == null || reporteMesasObservacionesDto.getDescripcionObservacion() == null ? null :  \" Pag. \" + reporteMesasObservacionesDto.getPagina() + \" : \" + reporteMesasObservacionesDto.getDescripcionObservacion())")
    @Mapping(target = "observacionMM", constant = "")
    @Mapping(target = "numeroMesa", source = "mesa")
    @Mapping(target = "descDepartamento", source = "departamento")
    @Mapping(target = "descProvincia", source = "provincia")
    @Mapping(target = "descDistrito", source = "distrito")
    @Mapping(target = "codiDesCompu", source = "codigoCentroComputo")
    @Mapping(target = "codiUbigeo", source = "codigoUbigeo")
    @Mapping(target = "nombreDocumento", source = "nombreDocumento")
    ReporteMesasObservacionesJasperDto toReporteMesasObservacionesJasperDto(ReporteMesasObservacionesDto reporteMesasObservacionesDto);

    List<ReporteMesasObservacionesJasperDto> toReporteMesasObservacionesJasperDtoList(List<ReporteMesasObservacionesDto> reporteMesasObservacionesDtoList);

}