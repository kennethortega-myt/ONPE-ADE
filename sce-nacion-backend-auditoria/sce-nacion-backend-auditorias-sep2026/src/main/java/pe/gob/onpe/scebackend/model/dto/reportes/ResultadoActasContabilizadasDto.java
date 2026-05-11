package pe.gob.onpe.scebackend.model.dto.reportes;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResultadoActasContabilizadasDto {
	
	private List<DetalleResultadosContabilizadas> detalleResultado;
	private List<DetalleResultadosContabilizadas> detalleTotal;
	private ResumenActasContabilizadas resumenActas;
	private Integer cantidadVotosPref;
	
}
