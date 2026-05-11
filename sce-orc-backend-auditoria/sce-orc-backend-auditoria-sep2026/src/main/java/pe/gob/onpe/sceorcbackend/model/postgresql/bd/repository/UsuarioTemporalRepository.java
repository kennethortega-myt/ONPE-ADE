package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.UsuarioTemporal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioTemporalRepository extends JpaRepository<UsuarioTemporal, Long> {
  UsuarioTemporal findByUsuario(String usuario);

}
