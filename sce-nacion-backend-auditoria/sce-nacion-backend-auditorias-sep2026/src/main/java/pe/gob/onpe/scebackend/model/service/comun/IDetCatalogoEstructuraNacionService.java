package pe.gob.onpe.scebackend.model.service.comun;

import pe.gob.onpe.scebackend.model.dto.request.comun.DetCatalogoEstructuraRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.DetCatalogoEstructuraResponseDto;

import java.util.List;

public interface IDetCatalogoEstructuraNacionService {
    List<DetCatalogoEstructuraResponseDto> listarDetCatalogoEstructura(DetCatalogoEstructuraRequestDto filtro);
}
