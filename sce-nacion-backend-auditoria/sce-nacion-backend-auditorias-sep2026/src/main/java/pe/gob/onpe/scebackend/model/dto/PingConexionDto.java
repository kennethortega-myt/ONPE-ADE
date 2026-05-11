package pe.gob.onpe.scebackend.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PingConexionDto {

	private Integer puerto;
	private String  ip;
	private String protocolo;
	
}
