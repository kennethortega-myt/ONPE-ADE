package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ResultadoActasContabilizadasDto {
	
	private List<DetalleResultadosContabilizadas> detalleResultado;
	private List<DetalleResultadosContabilizadas> detalleTotal;
	private ResumenActasContabilizadas resumenActas;
	private Integer cantidadVotosPref;
	
}
