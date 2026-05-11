package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteAuditoriaDigitacionActaCPRDto {

    private String codigoAmbitoElectoral;
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
    private String documentoIdentidad;
    private String nombresApellidos;
    private String totalVotos;
    private String cvas;
    private String votoOpcionSi;
    private String votoOpcionNo;
    private String votoOpcionBlanco;
    private String votoOpcionNulo;
    private String votoOpcionImpugnados;
    private String estadoActa;
    private String estadoActaDescripcion;
    private String estadoActaResolucion;
    private String estadoComputo;

}
