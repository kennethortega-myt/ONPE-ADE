package pe.gob.onpe.scebackend.model.dto.reportes;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstadoActasOdpeReporteDto {
	private List<DetalleEstadoActasOdpe> detalleEstadoActasOdpe;
	private List<DetalleEstadoActasOdpe> totalEstadoActasOdpe;
}
