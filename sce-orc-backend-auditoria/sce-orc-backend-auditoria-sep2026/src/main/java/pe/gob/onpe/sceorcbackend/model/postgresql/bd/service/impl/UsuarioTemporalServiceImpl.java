package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.Date;
import java.util.List;

import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.UsuarioTemporal;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.UsuarioTemporalRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UsuarioTemporalService;

import org.springframework.stereotype.Service;

@Service
public class UsuarioTemporalServiceImpl implements UsuarioTemporalService {

  private final UsuarioTemporalRepository usuarioTemporalRepository;

  public UsuarioTemporalServiceImpl(UsuarioTemporalRepository usuarioTemporalRepository) {
    this.usuarioTemporalRepository = usuarioTemporalRepository;
  }

  @Override
  public void save(UsuarioTemporal usuarioTemporal) {
    this.usuarioTemporalRepository.save(usuarioTemporal);
  }

  @Override
  public void saveAll(List<UsuarioTemporal> k) {
    this.usuarioTemporalRepository.saveAll(k);
  }

  @Override
  public void deleteAll() {
    this.usuarioTemporalRepository.deleteAll();
  }

  @Override
  public List<UsuarioTemporal> findAll() {
    return this.usuarioTemporalRepository.findAll();
  }

  @Override
  public UsuarioTemporal findByUsername(String usuario) {
    return this.usuarioTemporalRepository.findByUsuario(usuario);
  }

  @Override
  public GenericResponse<String> cerrarSession(String usuario) {
    GenericResponse<String> genericResponse = new GenericResponse<>();
    UsuarioTemporal tabUsuario = this.usuarioTemporalRepository.findByUsuario(usuario);
    if (tabUsuario != null) {
      tabUsuario.setSesionActiva(0);
      tabUsuario.setUsuarioModificacion(usuario);
      tabUsuario.setFechaModificacion(new Date());
      this.usuarioTemporalRepository.save(tabUsuario);
      genericResponse.setSuccess(true);
      genericResponse.setMessage("Sesión cerrada");
    }
    return genericResponse;
  }
  
  @Override
  public boolean estaActivo(String username) {
	  UsuarioTemporal usuario = this.usuarioTemporalRepository.findByUsuario(username);
    return usuario != null && !usuario.getSesionActiva().equals(0);
  }

  @Override
  public UsuarioTemporal saveUsuario(UsuarioTemporal usuarioTemporal) {
    return this.usuarioTemporalRepository.save(usuarioTemporal);
  }
}
