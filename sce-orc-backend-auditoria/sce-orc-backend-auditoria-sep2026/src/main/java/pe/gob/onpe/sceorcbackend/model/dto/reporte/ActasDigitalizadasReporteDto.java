package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Builder;
import lombok.Data;
import java.util.Date;

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
