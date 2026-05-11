package pe.gob.onpe.scebackend.ext.pr.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TramaVistaResponse {
    
	
	private List<TramaVistaDetalleResponse> detalle;
}
