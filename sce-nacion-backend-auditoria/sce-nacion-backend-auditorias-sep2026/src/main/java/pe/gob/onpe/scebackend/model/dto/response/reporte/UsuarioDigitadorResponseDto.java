package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioDigitadorResponseDto {
	
	private String codigoUsuario;
	private String documentoIdentidad;
	private String nombres;
	private String apellidoPaterno;
	private String apellidoMaterno;
}
