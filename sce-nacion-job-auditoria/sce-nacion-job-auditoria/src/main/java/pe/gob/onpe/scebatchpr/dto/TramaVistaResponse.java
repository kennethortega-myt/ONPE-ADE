package pe.gob.onpe.scebatchpr.dto;



import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TramaVistaResponse {
    
	
	private List<TramaVistaDetalleResponse> detalle;
}
