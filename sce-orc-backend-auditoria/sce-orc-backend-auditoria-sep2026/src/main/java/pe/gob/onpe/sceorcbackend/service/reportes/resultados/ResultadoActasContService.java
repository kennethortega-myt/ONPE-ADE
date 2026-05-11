package pe.gob.onpe.sceorcbackend.service.reportes.resultados;


import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroResultadoContabilizadasDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ResultadoActasContabilizadasDto;

public interface ResultadoActasContService {

	public ResultadoActasContabilizadasDto busquedaResultadosActasContabilizadas(FiltroResultadoContabilizadasDto filtros);
	
	public byte[] getReporteResultadoActasContabilizadas(FiltroResultadoContabilizadasDto filtro);
}
