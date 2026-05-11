package pe.gob.onpe.scebackend.model.dto.reportes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetalleEstadoActasOdpe {

	private Integer num;
	private String descOdpe;
	private String descCentroCompu;
	private Integer ahProcesar;
	private Integer porProcesar;
	private Integer procesadas;
	private Integer observadas;
	private Integer resueltas;
	private Integer pendienteResol;
	private String descEleccion;
}
