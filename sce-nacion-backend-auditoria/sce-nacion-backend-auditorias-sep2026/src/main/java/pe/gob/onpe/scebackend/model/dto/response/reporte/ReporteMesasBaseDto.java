package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteMesasBaseDto {
    private String nombreProcesoElectoral;
    private Integer idEleccion;
    private String nombreTipoEleccion;
    private String codigoCentroComputo;
    private String nombreCentroComputo;
    private String codigoAmbitoElectoral;
    private String nombreAmbitoElectoral;
    private String nombreUbigeoNivelUno;
    private Long totalMesasUbigeoNivelUno;
    private Long totalElectoresUbigeoNivelUno;
    private String nombreUbigeoNivelDos;
    private Long totalMesasUbigeoNivelDos;
    private Long totalElectoresUbigeoNivelDos;
    private String nombreUbigeoNivelTres;
    private Long totalMesasUbigeoNivelTres;
    private Long totalElectoresUbigeoNivelTres;
    private String codigoLocal;
    private String nombreLocal;
    private String direccion;
    private String mesa;
    private Long totalMesasLocal;
    private Long totalElectoresLocal;
    private Integer idEstadoActa;
    private String nombreEstadoActa;
}
