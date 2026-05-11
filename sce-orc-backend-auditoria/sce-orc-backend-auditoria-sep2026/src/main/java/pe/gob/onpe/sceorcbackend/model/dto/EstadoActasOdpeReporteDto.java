package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EstadoActasOdpeReporteDto {
	private List<DetalleEstadoActasOdpe> detalleEstadoActasOdpe;
	private List<DetalleEstadoActasOdpe> totalEstadoActasOdpe;
}
