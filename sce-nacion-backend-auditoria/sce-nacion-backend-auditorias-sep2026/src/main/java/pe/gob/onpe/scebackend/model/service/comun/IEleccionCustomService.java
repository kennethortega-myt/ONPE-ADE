package pe.gob.onpe.scebackend.model.service.comun;

import pe.gob.onpe.scebackend.model.dto.request.comun.EleccionRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.EleccionDto;

import java.util.List;

public interface IEleccionCustomService {
    List<EleccionDto> obtenerEleccionPorProcesoElectoralId(EleccionRequestDto filtro);

}
