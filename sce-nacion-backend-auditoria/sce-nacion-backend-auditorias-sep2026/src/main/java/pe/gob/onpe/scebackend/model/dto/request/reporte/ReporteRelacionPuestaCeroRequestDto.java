package pe.gob.onpe.scebackend.model.dto.request.reporte;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;

@Getter
@Setter
public class ReporteRelacionPuestaCeroRequestDto {
	private Long idProceso;
	@Alphanumeric
    private String esquema;
    private String proceso;
    private Long idCentroComputo;
    private String centroComputo;
    @Alphanumeric
    private String codigoCentroComputo;
    private Integer idEstado;
    private String estado;
    private String usuario;
}
