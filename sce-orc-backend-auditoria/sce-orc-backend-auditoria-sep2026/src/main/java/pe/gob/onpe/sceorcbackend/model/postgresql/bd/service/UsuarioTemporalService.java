package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.UsuarioTemporal;

public interface UsuarioTemporalService extends CrudService<UsuarioTemporal> {

  UsuarioTemporal findByUsername(String usuario);

  GenericResponse<String> cerrarSession(String usuario);
  
  public boolean estaActivo(String username);


  UsuarioTemporal saveUsuario(UsuarioTemporal usuarioTemporal);

}
