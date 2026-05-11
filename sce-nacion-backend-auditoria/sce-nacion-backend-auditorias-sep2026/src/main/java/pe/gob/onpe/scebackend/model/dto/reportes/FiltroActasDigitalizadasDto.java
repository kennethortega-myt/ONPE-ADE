package pe.gob.onpe.scebackend.model.dto.reportes;

import java.util.Date;

import lombok.Data;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;

@Data
public class FiltroActasDigitalizadasDto {

	@Alphanumeric
	private String esquema;
    private Integer idProceso;
    private Integer idEleccion;   
    private Integer idCentroComputo;
    private Integer idOdpe;
    private Date fechaInicial;
    private Date fechaFin;
    private String proceso;
    private String eleccion;
    private String centroComputo;
    private String odpe;
    private String usuario;    
}
