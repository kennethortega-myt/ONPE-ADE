package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;


import pe.gob.onpe.sceorcbackend.model.dto.reporte.DetCatalogoEstructuraRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.DetCatalogoEstructuraResponseDto;

import java.util.List;

public interface IDetCatalogoEstructuraNacionService {
    List<DetCatalogoEstructuraResponseDto> listarDetCatalogoEstructura(DetCatalogoEstructuraRequestDto filtro);
}
