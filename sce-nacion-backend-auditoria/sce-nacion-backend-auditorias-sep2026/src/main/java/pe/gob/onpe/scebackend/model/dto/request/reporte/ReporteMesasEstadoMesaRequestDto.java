package pe.gob.onpe.scebackend.model.dto.request.reporte;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;

@Getter
@Setter
public class ReporteMesasEstadoMesaRequestDto {
    @NotNull(message = "Campo Esquema es requerido")
    @NotBlank(message = "Esquema no contiene valor")
    @Alphanumeric
    private String esquema;
    @NotNull(message = "Campo Proceso es requerido")
    @NotBlank(message = "Proceso no contiene valor")
    @Alphanumeric
    private String proceso;
    private Integer idProceso;
    @NotNull(message = "Eleccion es requerido")
    @Alphanumeric
    private String eleccion;
    @NotNull(message = "Id Eleccion es requerido")
    private Integer idEleccion;
    @Alphanumeric
    private String codigoEleccion;
    @NotNull(message = "Tipo de Reporte es requerido")
    private Integer tipoReporte;
    @Alphanumeric
    private String codigoAmbitoElectoral;
    @Alphanumeric
    private String ambitoElectoral;
    @Alphanumeric
    private String codigoCentroComputo;
    @Alphanumeric
    private String centroComputo;
    @Alphanumeric
    private String codigoEstadoMesa;
    @Alphanumeric
    private String idEstadoMesa;
    private String estadoMesa;
    private Integer ubigeoNivelUno;
    private Integer ubigeoNivelDos;
    private Integer ubigeoNivelTres;
    @Alphanumeric
    private String usuario;
    private boolean porDistrito;
}
