package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import java.util.List;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.ProcesoAmbitoDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.ProcesoElectoralResponseDTO;
import pe.gob.onpe.sceorcbackend.model.dto.response.elecciones.EleccionDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ProcesoElectoral;

public interface MaeProcesoElectoralService extends CrudService<ProcesoElectoral> {

  List<ProcesoElectoral> findAll();

  List<ProcesoElectoralResponseDTO> findAll2();

  ProcesoElectoral findByActivo();

  public ProcesoAmbitoDto getTipoAmbito(String acronimo);

  public ProcesoAmbitoDto getTipoAmbitoPorIdProceso(Long idProceso);

  List<EleccionDto> getElecciones();

  ProcesoElectoral findBynActivo(Integer activo);
  
  boolean verificarHabilitacionDiaEleccion(String acronimo, String formatoFecha);
  
  long contarTodos();

}
