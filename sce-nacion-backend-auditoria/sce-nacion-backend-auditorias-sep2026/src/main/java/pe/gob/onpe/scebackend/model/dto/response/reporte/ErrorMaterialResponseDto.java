package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorMaterialResponseDto {

	private String codigo;
	private String nombre;
}
