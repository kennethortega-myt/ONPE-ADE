package pe.gob.onpe.scebackend.model.service.comun;

import pe.gob.onpe.scebackend.model.dto.request.comun.AmbitoElectoralRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.AmbitoElectoralDto;

import java.util.List;

public interface IAmbitoElectoralNacionService {
    List<AmbitoElectoralDto> listarAmbitoElectoralPorCentroComputo(AmbitoElectoralRequestDto filtro);
    List<AmbitoElectoralDto> listarAmbitoElectoralPorEleccion(AmbitoElectoralRequestDto filtro);
}
