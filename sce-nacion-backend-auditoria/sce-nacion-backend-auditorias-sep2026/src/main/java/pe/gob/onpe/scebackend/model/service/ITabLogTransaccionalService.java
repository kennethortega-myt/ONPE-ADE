package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.scebackend.model.orc.entities.TabLogTransaccional;

public interface ITabLogTransaccionalService extends CrudService<TabLogTransaccional> {

  void registrarLog(String functionName, String serviceName, String message, String ambitoElectoral, String centroComputo,
      Integer autorizacion, Integer accion);

  void registrarLog(String usuario, String functionName, String serviceName, String message, String ambitoElectoral, String centroComputo,
      Integer autorizacion, Integer accion);

  SearchFilterResponse<TabLogTransaccional> listPaginted(String error, Integer page, Integer size);
}
