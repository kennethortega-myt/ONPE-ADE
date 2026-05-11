package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.orc.entities.Usuario;
import pe.gob.onpe.scebackend.security.dto.GenericResponse;

public interface UsuarioService extends CrudService<Usuario> {
  Usuario findByUsername(String usuario);
  GenericResponse<String> cerrarSession(String usuario);

}
