package pe.gob.onpe.scebackend.model.dto.reportes;

import lombok.Data;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;

@Data
public class FiltroEstadoActasOdpeDto {

	@Alphanumeric
	private String esquema;
    private Integer idProceso;
    private Integer idEleccion;   
    private Integer idCentroComputo;
    private Integer idOdpe;
    private String proceso;
    private String eleccion;
    private String centroComputo;
    private String odpe;
    private String usuario;
    private String codigoCentroComputo;
}
