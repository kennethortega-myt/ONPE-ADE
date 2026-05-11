package pe.gob.onpe.scebackend.model.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.gob.onpe.scebackend.model.dto.reportes.AuditoriaDigitacionJasperDto;
import pe.gob.onpe.scebackend.model.dto.reportes.AuditoriaDigitacionPreferencialJasperDto;
import pe.gob.onpe.scebackend.model.dto.reportes.AuditoriaDigitacionCPRJasperDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteAuditoriaDigitacionActaDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteAuditoriaDigitacionActaPrefencialDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteAuditoriaDigitacionActaCPRDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReporteAuditoriaDigitacionMapper {

    @Mapping(target = "nro", expression = "java(String.valueOf(reporteAuditoriaDigitacionActaDto.getPosicion()))")
    @Mapping(target = "organizacionPolitica", source = "descripcionAgrupacionPolitica")
    @Mapping(target = "votosDigitados", expression = "java(reporteAuditoriaDigitacionActaDto.getTotalVotos() != null ? reporteAuditoriaDigitacionActaDto.getTotalVotos().toString() : \"\")")
    @Mapping(target = "descCompu", source = "nombreCentroComputo")
    @Mapping(target = "codigoNumeroActa", expression = "java(reporteAuditoriaDigitacionActaDto.getNumeroCopiaActa() == null || reporteAuditoriaDigitacionActaDto.getNumeroCopiaActa().trim().equals(\"\") ? reporteAuditoriaDigitacionActaDto.getMesa() : reporteAuditoriaDigitacionActaDto.getMesa() + \" - \" + reporteAuditoriaDigitacionActaDto.getNumeroCopiaActa() + reporteAuditoriaDigitacionActaDto.getDigitoChequeoEscrutinio())")
    @Mapping(target = "descOdpe", source = "nombreAmbitoElectoral")
    @Mapping(target = "cvas", source = "cvas", defaultValue = "")
    @Mapping(target = "codUbigeo", source = "codigoUbigeo", defaultValue = "")
    AuditoriaDigitacionJasperDto toAuditoriaDigitacionJasperDto(ReporteAuditoriaDigitacionActaDto reporteAuditoriaDigitacionActaDto);

    List<AuditoriaDigitacionJasperDto> toAuditoriaDigitacionJasperDtoList(List<ReporteAuditoriaDigitacionActaDto> reporteAuditoriaDigitacionActaDtoList);

    @Mapping(target = "nro", expression = "java(String.valueOf(reporteAuditoriaDigitacionActaPrefencialDto.getPosicion()))")
    @Mapping(target = "organizacionPolitica", source = "descripcionAgrupacionPolitica")
    @Mapping(target = "tvotos", expression = "java(reporteAuditoriaDigitacionActaPrefencialDto.getTotalVotos() != null ? reporteAuditoriaDigitacionActaPrefencialDto.getTotalVotos().toString() : \"\")")
    @Mapping(target = "descCompu", source = "nombreCentroComputo")
    @Mapping(target = "codigoNumeroActa", expression = "java(reporteAuditoriaDigitacionActaPrefencialDto.getNumeroCopiaActa() == null || reporteAuditoriaDigitacionActaPrefencialDto.getNumeroCopiaActa().trim().equals(\"\") ? reporteAuditoriaDigitacionActaPrefencialDto.getMesa() : reporteAuditoriaDigitacionActaPrefencialDto.getMesa() + \" - \" + reporteAuditoriaDigitacionActaPrefencialDto.getNumeroCopiaActa() + reporteAuditoriaDigitacionActaPrefencialDto.getDigitoChequeoEscrutinio())")
    @Mapping(target = "descOdpe", source = "nombreAmbitoElectoral")
    @Mapping(target = "codUbigeo", source = "codigoUbigeo")
    @Mapping(target = "nvoto1", source = "votoPreferencial01", defaultValue = "")
    @Mapping(target = "nvoto2", source = "votoPreferencial02", defaultValue = "")
    @Mapping(target = "nvoto3", source = "votoPreferencial03", defaultValue = "")
    @Mapping(target = "nvoto4", source = "votoPreferencial04", defaultValue = "")
    @Mapping(target = "nvoto5", source = "votoPreferencial05", defaultValue = "")
    @Mapping(target = "nvoto6", source = "votoPreferencial06", defaultValue = "")
    @Mapping(target = "nvoto7", source = "votoPreferencial07", defaultValue = "")
    @Mapping(target = "nvoto8", source = "votoPreferencial08", defaultValue = "")
    @Mapping(target = "nvoto9", source = "votoPreferencial09", defaultValue = "")
    @Mapping(target = "nvoto10", source = "votoPreferencial10", defaultValue = "")
    @Mapping(target = "nvoto11", source = "votoPreferencial11", defaultValue = "")
    @Mapping(target = "nvoto12", source = "votoPreferencial12", defaultValue = "")
    @Mapping(target = "nvoto13", source = "votoPreferencial13", defaultValue = "")
    @Mapping(target = "nvoto14", source = "votoPreferencial14", defaultValue = "")
    @Mapping(target = "nvoto15", source = "votoPreferencial15", defaultValue = "")
    @Mapping(target = "nvoto16", source = "votoPreferencial16", defaultValue = "")
    @Mapping(target = "nvoto17", source = "votoPreferencial17", defaultValue = "")
    @Mapping(target = "nvoto18", source = "votoPreferencial18", defaultValue = "")
    @Mapping(target = "nvoto19", source = "votoPreferencial19", defaultValue = "")
    @Mapping(target = "nvoto20", source = "votoPreferencial20", defaultValue = "")
    @Mapping(target = "nvoto21", source = "votoPreferencial21", defaultValue = "")
    @Mapping(target = "nvoto22", source = "votoPreferencial22", defaultValue = "")
    @Mapping(target = "nvoto23", source = "votoPreferencial23", defaultValue = "")
    @Mapping(target = "nvoto24", source = "votoPreferencial24", defaultValue = "")
    @Mapping(target = "nvoto25", source = "votoPreferencial25", defaultValue = "")
    @Mapping(target = "nvoto26", source = "votoPreferencial26", defaultValue = "")
    @Mapping(target = "nvoto27", source = "votoPreferencial27", defaultValue = "")
    @Mapping(target = "nvoto28", source = "votoPreferencial28", defaultValue = "")
    @Mapping(target = "nvoto29", source = "votoPreferencial29", defaultValue = "")
    @Mapping(target = "nvoto30", source = "votoPreferencial30", defaultValue = "")
    @Mapping(target = "nvoto31", source = "votoPreferencial31", defaultValue = "")
    @Mapping(target = "nvoto32", source = "votoPreferencial32", defaultValue = "")
    @Mapping(target = "nvoto33", source = "votoPreferencial33", defaultValue = "")
    @Mapping(target = "nvoto34", source = "votoPreferencial34", defaultValue = "")
    @Mapping(target = "nvoto35", source = "votoPreferencial35", defaultValue = "")
    @Mapping(target = "nvoto36", source = "votoPreferencial36", defaultValue = "")
    @Mapping(target = "cvas", source = "cvas", defaultValue = "")
    AuditoriaDigitacionPreferencialJasperDto toAuditoriaDigitacionPreferencialJasperDto(ReporteAuditoriaDigitacionActaPrefencialDto reporteAuditoriaDigitacionActaPrefencialDto);

    List<AuditoriaDigitacionPreferencialJasperDto> toAuditoriaDigitacionPreferencialJasperDtoList(List<ReporteAuditoriaDigitacionActaPrefencialDto> reporteAuditoriaDigitacionActaPrefencialDtoList);

    @Mapping(target = "codOdpe", source = "codigoAmbitoElectoral", defaultValue = "")
    @Mapping(target = "descOdpe", source = "nombreAmbitoElectoral", defaultValue = "")
    @Mapping(target = "codCentCompu", source = "codigoCentroComputo", defaultValue = "")
    @Mapping(target = "descCentCompu", source = "nombreCentroComputo", defaultValue = "")
    @Mapping(target = "departamento", source = "departamento", defaultValue = "")
    @Mapping(target = "provincia", source = "provincia", defaultValue = "")
    @Mapping(target = "distrito", source = "distrito", defaultValue = "")
    @Mapping(target = "numActa", source = "mesa", defaultValue = "")
    @Mapping(target = "copiaActa", source = "numeroCopiaActa", defaultValue = "")
    @Mapping(target = "digitoChequeo", source = "digitoChequeoEscrutinio", defaultValue = "")
    @Mapping(target = "ubicacionAgrupol", source = "posicion")
    @Mapping(target = "descAgrupol", source = "nombresApellidos", defaultValue = "")
    @Mapping(target = "cvas", source = "cvas", defaultValue = "")
    @Mapping(target = "votosSI", source = "votoOpcionSi", defaultValue = "")
    @Mapping(target = "votosNO", source = "votoOpcionNo", defaultValue = "")
    @Mapping(target = "votosBL", source = "votoOpcionBlanco", defaultValue = "")
    @Mapping(target = "votosNL", source = "votoOpcionNulo", defaultValue = "")
    @Mapping(target = "votosIM", source = "votoOpcionImpugnados", defaultValue = "")
    @Mapping(target = "estadoActa", source = "estadoActa", defaultValue = "")
    @Mapping(target = "estadoRes", source = "estadoActaResolucion", defaultValue = "")
    @Mapping(target = "estadoCompu", source = "estadoComputo", defaultValue = "")
    AuditoriaDigitacionCPRJasperDto toAuditoriaDigitacionCPRJasperDto(ReporteAuditoriaDigitacionActaCPRDto reporteAuditoriaDigitacionActaRevocatoriaDto);

    List<AuditoriaDigitacionCPRJasperDto> toAuditoriaDigitacionCPRJasperDtoList(List<ReporteAuditoriaDigitacionActaCPRDto> reporteAuditoriaDigitacionActaRevocatoriaDtoList);


}