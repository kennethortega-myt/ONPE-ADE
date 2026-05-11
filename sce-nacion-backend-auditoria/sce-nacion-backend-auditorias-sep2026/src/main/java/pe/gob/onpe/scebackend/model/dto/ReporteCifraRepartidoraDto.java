package pe.gob.onpe.scebackend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteCifraRepartidoraDto {
	
	private String organizacionPolitica;
	private Integer votosValidos;
	private String votosCifraRepartidora;
	private String cociente;
	private Integer nroRepresentantes;
	private String columnObservaciones;
	private String codDistritoElectoral;
	private String descDistritoElectoral;
	private String factorCr;
	private Integer nroEscanos;
	private String estadoCr;
	private String fechaProcCr;
	private Integer nroCurules;
}
