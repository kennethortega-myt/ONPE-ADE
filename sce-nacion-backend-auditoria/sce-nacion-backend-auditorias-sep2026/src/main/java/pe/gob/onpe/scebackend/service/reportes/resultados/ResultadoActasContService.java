package pe.gob.onpe.scebackend.service.reportes.resultados;


import pe.gob.onpe.scebackend.model.dto.reportes.FiltroResultadoContabilizadasDto;
import pe.gob.onpe.scebackend.model.dto.reportes.ResultadoActasContabilizadasDto;

public interface ResultadoActasContService {

	public ResultadoActasContabilizadasDto busquedaResultadosActasContabilizadas(FiltroResultadoContabilizadasDto filtros);
	
	public byte[] getReporteResultadoActasContabilizadas(FiltroResultadoContabilizadasDto filtro);
}
