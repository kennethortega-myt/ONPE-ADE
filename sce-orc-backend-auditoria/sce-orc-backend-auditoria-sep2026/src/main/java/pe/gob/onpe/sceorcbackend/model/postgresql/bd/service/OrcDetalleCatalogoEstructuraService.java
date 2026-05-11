package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OrcDetalleCatalogoEstructura;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura.DetCatalogoEstructuraDTO;

import java.util.List;

public interface OrcDetalleCatalogoEstructuraService extends CrudService<OrcDetalleCatalogoEstructura> {
    List<DetCatalogoEstructuraDTO> findByMaestroAndColumna(String cMaestro, String columna);
}
