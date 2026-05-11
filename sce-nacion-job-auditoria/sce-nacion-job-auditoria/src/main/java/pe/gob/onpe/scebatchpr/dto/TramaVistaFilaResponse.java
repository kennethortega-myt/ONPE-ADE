package pe.gob.onpe.scebatchpr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TramaVistaFilaResponse {

	 private Integer  idFila;
	 private boolean recibido;
	 private String mensaje;
	
}
