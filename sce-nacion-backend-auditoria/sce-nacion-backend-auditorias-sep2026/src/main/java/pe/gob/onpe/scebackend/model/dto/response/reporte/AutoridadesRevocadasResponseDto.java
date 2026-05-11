package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AutoridadesRevocadasResponseDto {

	private String departamento;
	private String provincia;
	private Integer orden;
	private String distrito;
	private String descCargo;
	private String nombre;
	private Integer votosSI;
	private Integer votosNO;
	private Integer elecHabil;
	private String codCargo;
	private Integer totCiudadVotaron;
	
}
