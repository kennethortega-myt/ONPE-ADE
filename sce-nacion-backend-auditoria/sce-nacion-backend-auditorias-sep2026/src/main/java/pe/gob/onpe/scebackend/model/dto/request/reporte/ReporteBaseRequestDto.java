package pe.gob.onpe.scebackend.model.dto.request.reporte;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;

@Getter
@Setter
public class ReporteBaseRequestDto {
	@NotNull(message = "Campo Esquema es requerido")
    @NotBlank(message = "Esquema no contiene valor")
    @Alphanumeric
    private String esquema;
    @Alphanumeric
    private String eleccion;
    private Integer idProceso;
    private Integer idEleccion;
    private Integer idAmbitoElectoral;
    @Alphanumeric
    private String codigoAmbitoElectoral;
    private Integer idCentroComputo;
    @Alphanumeric
    private String codigoCentroComputo;
    private Integer idUbigeoNivelUno;
    private Integer idUbigeoNivelDos;
    private Integer idUbigeoNivelTres;
    @Alphanumeric
    private String usuario;
    private String acronimo;
}
