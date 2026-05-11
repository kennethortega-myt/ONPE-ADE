package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;


import pe.gob.onpe.sceorcbackend.model.dto.CentroComputoDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.CentroComputoRequestDto;

import java.util.List;

public interface ICentroComputoNacionService {
    List<CentroComputoDto> listarCentroComputo(CentroComputoRequestDto filtro);
    List<CentroComputoDto> listarCentroComputoPorEleccion(CentroComputoRequestDto filtro);
    List<CentroComputoDto> listarCentroComputoPorAmbitoElectoral(CentroComputoRequestDto filtro);
}
