package pe.gob.onpe.scebackend.model.service.impl;



import java.util.Date;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.orc.entities.Usuario;
import pe.gob.onpe.scebackend.model.orc.entities.UsuarioTemporal;
import pe.gob.onpe.scebackend.model.orc.repository.UsuarioRepository;
import pe.gob.onpe.scebackend.model.orc.repository.UsuarioTemporalRepository;
import pe.gob.onpe.scebackend.model.service.UsuarioService;
import pe.gob.onpe.scebackend.security.dto.GenericResponse;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

@Service
public class UsuarioServiceImpl implements UsuarioService {
  Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);
  private final UsuarioRepository usuarioRepository;
  private final UsuarioTemporalRepository usuarioTemporalRepository;

  public UsuarioServiceImpl(UsuarioRepository usuarioRepository,UsuarioTemporalRepository usuarioTemporalRepository) {
    this.usuarioRepository = usuarioRepository;
    this.usuarioTemporalRepository = usuarioTemporalRepository;
  }

  @Override
  public void save(Usuario usuario) {
    this.usuarioRepository.save(usuario);
  }

  @Override
  public void saveAll(List<Usuario> k) {
    this.usuarioRepository.saveAll(k);
  }

  @Override
  public void deleteAll() {
    this.usuarioRepository.deleteAll();
  }

  @Override
  public List<Usuario> findAll() {
    return this.usuarioRepository.findAll();
  }

  @Override
  public Usuario findByUsername(String usuario) {
    return this.usuarioRepository.findByUsuario(usuario);
  }

  @Override
  public GenericResponse<String> cerrarSession(String usuario) {
    GenericResponse<String> genericResponse = new GenericResponse<>();
    try {
        Usuario tabUsuario = this.usuarioRepository.findByUsuario(usuario);
        UsuarioTemporal tabUsuarioTemporal = this.usuarioTemporalRepository.findByUsuario(usuario);
        if (tabUsuario != null) {
            tabUsuario.setSesionActiva(0);
            tabUsuario.setUsuarioModificacion(usuario);
            tabUsuario.setFechaModificacion(new Date());
            this.usuarioRepository.save(tabUsuario);
        }

        if (tabUsuarioTemporal != null) {
            tabUsuarioTemporal.setSesionActiva(0);
            tabUsuarioTemporal.setUsuarioModificacion(usuario);
            tabUsuarioTemporal.setFechaModificacion(new Date());
            this.usuarioTemporalRepository.save(tabUsuarioTemporal);
        }
        genericResponse.setSuccess(true);
        genericResponse.setMessage("Sesión cerrada.");
    } catch (Exception e) {
        logger.error("Error al cerrar sesión: ", e);
        genericResponse.setSuccess(true);
        genericResponse.setMessage("Sesión cerrada.");
    }

    return genericResponse;
  }

}
