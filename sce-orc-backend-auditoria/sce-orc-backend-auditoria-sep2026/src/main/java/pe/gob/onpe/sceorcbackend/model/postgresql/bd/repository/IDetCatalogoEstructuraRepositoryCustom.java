package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;


import pe.gob.onpe.sceorcbackend.model.dto.reporte.DetCatalogoEstructuraRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.DetCatalogoEstructuraResponseDto;

import java.util.List;

public interface IDetCatalogoEstructuraRepositoryCustom {
    List<DetCatalogoEstructuraResponseDto> listarDetalleEstructura(DetCatalogoEstructuraRequestDto param);
}
