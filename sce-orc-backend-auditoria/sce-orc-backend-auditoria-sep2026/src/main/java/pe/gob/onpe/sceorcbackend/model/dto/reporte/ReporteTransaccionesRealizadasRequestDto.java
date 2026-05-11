package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteTransaccionesRealizadasRequestDto extends ReporteBaseRequestDto{
	private String proceso;
	private Date fechaDesde;
	private String horaDesde;
	private Date fechaHasta;
	private String horaHasta;
	private Boolean soloConAutorizacion;
	private String usuarioVisualizacion;
	private String nombreCentroComputo;
}
