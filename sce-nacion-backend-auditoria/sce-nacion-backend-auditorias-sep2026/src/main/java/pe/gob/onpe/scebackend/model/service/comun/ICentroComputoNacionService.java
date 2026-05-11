package pe.gob.onpe.scebackend.model.service.comun;

import pe.gob.onpe.scebackend.model.dto.request.comun.CentroComputoRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.CentroComputoDto;

import java.util.List;

public interface ICentroComputoNacionService {
    List<CentroComputoDto> listarCentroComputo(CentroComputoRequestDto filtro);
    List<CentroComputoDto> listarCentroComputoPorEleccion(CentroComputoRequestDto filtro);
    List<CentroComputoDto> listarCentroComputoPorAmbitoElectoral(CentroComputoRequestDto filtro);
}
