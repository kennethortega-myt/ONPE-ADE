package pe.gob.onpe.scebackend.model.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.dto.DetCatalogoEstructuraDto;
import pe.gob.onpe.scebackend.model.orc.entities.OrcDetalleCatalogoEstructura;

public interface IDetalleCatalogoEstructuraService extends CrudService<OrcDetalleCatalogoEstructura> {
    List<DetCatalogoEstructuraDto> findByMaestroAndColumna(String cMaestro, String cColumna);

}
