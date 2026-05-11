package pe.gob.onpe.scebackend.model.dto.reportes;

import lombok.Data;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;

@Data
public class FiltroAvanceDigitalizacionDto {

	@Alphanumeric
	private String esquema;
    private Integer idProceso;
    private Integer idEleccion;
    private Integer centroComputo;
    @Alphanumeric
    private String ubigeo;
    private String proceso;
    private String usuario;
    private boolean sobreCeleste;
  
}
