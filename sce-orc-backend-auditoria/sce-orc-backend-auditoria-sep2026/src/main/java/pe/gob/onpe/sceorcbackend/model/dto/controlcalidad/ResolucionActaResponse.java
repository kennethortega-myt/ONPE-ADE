package pe.gob.onpe.sceorcbackend.model.dto.controlcalidad;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResolucionActaResponse {
	private Long idDetActaResol;
	private Long idActa;
	private Long idResolucion;
	private Long idArchivo;
	private String nombreResolucion;
	private Long numeroElectores;
	private Long numeroElectoresAusentes;
	private String numeroExpediente;
}
