package pe.gob.onpe.scebackend.model.dto.reportes;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActasDigitalizadasReporteDto {

	private String numActa;
	private String copiaActa;
	private String digitoChequeo;
	private String codDepartamento;
	private String descDepartamento;
	private String codProvincia;
	private String descProvincia;
	private String codDistrito;
	private String descDistrito;
	private String localVotacion;
	private Date fechaDigtal;
	private String ubigeo;
	private String codCentroComputo;
	private String centroComputo;
	
}
