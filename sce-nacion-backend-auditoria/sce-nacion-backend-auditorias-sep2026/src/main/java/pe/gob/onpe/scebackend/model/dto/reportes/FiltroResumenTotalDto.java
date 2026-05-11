package pe.gob.onpe.scebackend.model.dto.reportes;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;

@Getter
@Setter
public class FiltroResumenTotalDto {

	@Alphanumeric
	private String esquema;
	private Integer idProceso;
    private Integer idEleccion;
    private String codigoEleccion;
    private Integer idCentroComputo;
    private Integer idOdpe;
    private Integer habilitado;
    private Integer tipoReporte;
    private String proceso;
    private String eleccion;
    @Alphanumeric
    private String usuario;
    
    private String centroComputo;
    @Alphanumeric
    private String estado;
    
}
