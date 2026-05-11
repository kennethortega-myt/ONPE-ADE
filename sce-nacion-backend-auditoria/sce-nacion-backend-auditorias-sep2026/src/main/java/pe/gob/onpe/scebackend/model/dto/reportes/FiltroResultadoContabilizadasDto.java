package pe.gob.onpe.scebackend.model.dto.reportes;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;

@Getter
@Setter
public class FiltroResultadoContabilizadasDto {

	@Alphanumeric
	private String esquema;
    private Integer idProceso;
    private Integer idEleccion;
    private Integer idCentroComputo;
    private Integer idOdpe;
    @Alphanumeric
    private String ubigeo;
    private String proceso;
    private String eleccion;
    @Alphanumeric
    private String usuario;
    private Integer tipoReporte;
    private String cc;
    @Alphanumeric
    private String codigoEleccion;
    @Alphanumeric
    private String acronimo;
    private String centroComputo;
    private String odpe;
    private String distritoElectoral;
    private String nombreUbigeoUno;
    private String nombreUbigeoDos;
    private String nombreUbigeoTres;
}
