package pe.gob.onpe.scebackend.model.dto.request.reporte;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;

@Getter
@Setter
public abstract class BaseRequestDto {
    @NotNull(message = "Campo Esquema es requerido")
    @NotBlank(message = "Esquema no contiene valor")
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
    
    private Integer idCentroComputo;
    
    @Alphanumeric
    private String centroComputo;
    
    @Alphanumeric
    private String codigoCentroComputo;
    
    @Alphanumeric
    private String usuario;

    private String acronimo;
}
