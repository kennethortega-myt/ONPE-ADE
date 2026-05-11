package pe.gob.onpe.scebackend.model.dto.request.reporte;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;

@Getter
@Setter
public class ReporteMesasUbigeoRequestDto {
	private Long idProceso;
    private String eleccion;
    @Alphanumeric
    private String esquema;
    @Alphanumeric
    private String centroComputo;
    @Alphanumeric
    private String departamento;
    @Alphanumeric
    private String provincia;
    @Alphanumeric
    private String distrito;
    private String usuario;
    private String proceso;
}
