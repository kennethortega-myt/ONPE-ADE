package pe.gob.onpe.scebackend.model.orc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.scebackend.model.orc.entities.UsuarioTemporal;

public interface UsuarioTemporalRepository extends JpaRepository<UsuarioTemporal, Long> {
  UsuarioTemporal findByUsuario(String usuario);
}
