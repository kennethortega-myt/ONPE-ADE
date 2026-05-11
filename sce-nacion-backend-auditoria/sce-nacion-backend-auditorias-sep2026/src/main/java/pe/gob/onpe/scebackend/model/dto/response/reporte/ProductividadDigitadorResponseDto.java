package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductividadDigitadorResponseDto {

	private String codigoCentroComputo;
	private String usuario;
	private String nombres;
	private Long totalDigitaciones;
	private String tiempoTotal;
}
