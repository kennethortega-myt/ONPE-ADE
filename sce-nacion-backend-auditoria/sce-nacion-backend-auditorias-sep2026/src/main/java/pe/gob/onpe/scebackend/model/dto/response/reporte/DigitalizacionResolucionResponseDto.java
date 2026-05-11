package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DigitalizacionResolucionResponseDto {

	private String codiDesOdpe;
	private String codiDesCompu;
	private String estadoDigital;
	private String numeResolucionJNE;
	private String codCCompu;
	private String codODPE;
	private String codigoUbigeo;
}
