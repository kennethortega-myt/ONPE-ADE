package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteMesasEstadoMesaDto {
    private String nombreProcesoElectoral;
    private Integer idEleccion;

    private String nombreTipoEleccion;
    private String codigoCentroComputo;
    private String nombreCentroComputo;

    private String codigoAmbitoElectoral;
    private String nombreAmbitoElectoral;

    private String nombreDepartamento;
    private Integer mesasDep;
    private Integer electoresDep;

    private String nombreProvincia;
    private Integer mesasPro;
    private Integer electoresPro;

    private String nombreDistrito;
    private Integer mesasDis;
    private Integer electoresDis;

    private String codigoLocal;
    private String nombreLocal;
    private String direccion;

    private String mesa;
    private Integer mesasLocal;

    private Integer electoresLocal;
    private String estadoMesaDis;

}
