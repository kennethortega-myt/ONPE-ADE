package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabLog;

public interface ITabLogService extends CrudService<TabLog> {

  void registrarLog(String usuario, String functionName, String serviceName, String message, String centroComputo, Integer autorizacion, Integer accion);

  SearchFilterResponse<TabLog> listPaginted(String error, Integer page, Integer size);

  void registrarLog(String usuario, String functionName, String message, String centroComputo, Integer autorizacion, Integer accion);
  
  void deleteByFechaRegistroBefore();

}
