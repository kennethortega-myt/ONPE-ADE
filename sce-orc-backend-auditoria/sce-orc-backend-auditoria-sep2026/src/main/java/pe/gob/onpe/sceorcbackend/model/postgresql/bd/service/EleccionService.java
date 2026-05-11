package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import java.util.List;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.response.elecciones.EleccionResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;

public interface EleccionService extends CrudService<Eleccion> {

  List<Eleccion> findEleccionesByProceso(Long idProceso);

  List<EleccionResponseDto> findEleccionesByProceso2(Long idProceso);

  Eleccion obtenerEleccionPrincipalPorProceso(Long idProceso);

}
