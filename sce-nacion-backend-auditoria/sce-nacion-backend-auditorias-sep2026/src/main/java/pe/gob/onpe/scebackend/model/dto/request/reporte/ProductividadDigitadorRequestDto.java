package pe.gob.onpe.scebackend.model.dto.request.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductividadDigitadorRequestDto extends ReporteBaseRequestDto {
	
	private String usuarioDigitador;
	private String proceso;
	private String centroComputo;
}
