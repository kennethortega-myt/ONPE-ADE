package pe.gob.onpe.scebackend.model.orc.repository.comun;


import pe.gob.onpe.scebackend.model.dto.request.comun.DetCatalogoEstructuraRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.DetCatalogoEstructuraResponseDto;

import java.util.List;

public interface IDetCatalogoEstructuraRepositoryCustom {
    List<DetCatalogoEstructuraResponseDto> listarDetalleEstructura(DetCatalogoEstructuraRequestDto param);
}
