package pe.gob.onpe.scebackend.model.dto.reportes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BarreraElectoralDto {
	
	private String estadoValla;
	private Double porcentajeVotosValidos;
	private String codigoAgrupol;
	private String descripcionAgrupol;
	private Integer votosValidos;
	private Integer escanosObtenidos;
	private Integer totalMiembros;
	//Para senadores	
	private Integer miembrosUnico;
	private Integer miembrosLima;
	private Integer miembrosOtros;
	private Integer votosValidosMultiple;
	private Integer votosValidosUnico;

}
