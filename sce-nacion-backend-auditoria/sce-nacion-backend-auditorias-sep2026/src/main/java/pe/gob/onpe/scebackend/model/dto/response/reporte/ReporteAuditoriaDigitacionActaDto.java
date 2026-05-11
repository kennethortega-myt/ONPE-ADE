package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteAuditoriaDigitacionActaDto {

    private String ambitoElectoral;
    private String nombreAmbitoElectoral;
    private String codigoCentroComputo;
    private String nombreCentroComputo;
    private String codigoUbigeo;
    private String departamento;
    private String provincia;
    private String distrito;
    private Long numeroActa;
    private String numeroCopiaActa;
    private String mesa;
    private String digitoChequeoEscrutinio;
    private Short posicion;
    private String descripcionAgrupacionPolitica;
    private String totalVotos;
    private String cvas;

}
