package pe.gob.onpe.scebackend.model.service;

import java.io.FileNotFoundException;
import java.util.List;

import pe.gob.onpe.scebackend.model.dto.*;
import pe.gob.onpe.scebackend.model.dto.request.FiltrosActaNacionRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.orc.entities.Acta;


public interface IActaService {
	
	void save(Acta acta);
	CabActaDto save(CabActaDto acta) throws FileNotFoundException;
	List<AvanceMesaReporteDto> getAvanceMesa(FiltroAvanceMesaDto filtro);
	List<Acta> listActa(FiltrosActaNacionRequestDto filtros);
	String transmisionRecepcionDto(Long id);
	ReturnMonitoreoActas listActasMonitoreo(MonitoreoNacionBusquedaDto monitoreoNacionBusqueda);
	PaginacionDetalleDto informacionPaginacion(MonitoreoNacionBusquedaDto monitoreoNacionBusqueda,Integer resultadoPorPagina);

    GenericResponse trazabilidadActa(String mesaCopiaDigito);
}
