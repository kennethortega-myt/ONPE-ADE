package pe.gob.onpe.scebackend.ext.pr.dto;

import java.util.List;

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
public class TramaVistaDetalleResponse {

	private Long  idTransferencia;
    private boolean recibido;
    private List<TramaVistaFilaResponse> filas;
	
}
