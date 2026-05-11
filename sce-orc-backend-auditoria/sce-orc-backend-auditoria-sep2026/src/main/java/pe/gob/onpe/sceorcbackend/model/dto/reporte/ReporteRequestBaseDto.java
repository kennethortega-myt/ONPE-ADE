package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Getter
@Setter
@ToString
public class ReporteRequestBaseDto {
    @Alphanumeric
    private String esquema;
    @NotNull(message = "Id Proceso es requerido")
    private Integer idProceso;
    @NotNull(message = "Campo Proceso es requerido")
    @NotBlank(message = "Proceso no contiene valor")
    @Alphanumeric
    private String proceso;
    @NotNull(message = "Eleccion es requerido")
    @Alphanumeric
    private String eleccion;
    @NotNull(message = "Id Eleccion es requerido")
    private Integer idEleccion;
    @Alphanumeric
    private String codigoEleccion;
    private Integer idCentroComputo;
    @Alphanumeric
    private String codigoCentroComputo;
    @Alphanumeric
    private String centroComputo;
    @Alphanumeric
    private String codigoAmbitoElectoral;
    @Alphanumeric
    private String ambitoElectoral;
    @Alphanumeric
    private String usuario;
    private Integer tipoReporteActorElectoral;
    private String acronimo;
}
