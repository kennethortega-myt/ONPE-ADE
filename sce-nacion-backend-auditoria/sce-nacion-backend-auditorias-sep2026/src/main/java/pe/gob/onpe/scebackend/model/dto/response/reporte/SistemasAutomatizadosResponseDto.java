package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SistemasAutomatizadosResponseDto {

	private String eleccion;
	private String descOdpe;
	private String descCC;
	private String departamento;
	private String provincia;
	private String distrito;
	private String ubigeo;
	private String numeMesa;
	private String estado;
	private String solucion;
	
}
