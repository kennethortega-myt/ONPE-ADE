package pe.gob.onpe.scebackend.model.dto.request.reporte;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class ReporteMesasEstadoActaRequestDto {
    @NotNull(message = "Campo Esquema es requerido")
    @NotBlank(message = "Esquema no contiene valor")
    private String esquema;
    @NotNull(message = "Campo Proceso es requerido")
    @NotBlank(message = "Proceso no contiene valor")
    private String proceso;
    @NotNull(message = "Eleccion es requerido")
    private Integer eleccion;
    @NotNull(message = "Tipo de Reporte es requerido")
    private Integer tipoReporte;
    private String ambitoElectoral;
    private String centroComputo;
    private Integer estadoMesa;
    private Integer ubigeoNivelUno;
    private Integer ubigeoNivelDos;
    private Integer ubigeoNivelTres;
}
