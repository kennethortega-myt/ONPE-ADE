package pe.gob.onpe.scebatchpr.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TramaVistaDetalleResponse {

	private Long  idTransferencia;
    private boolean recibido;
    private String mensaje;
    private List<TramaVistaFilaResponse> filas;
	
}
