package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.usuario;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;

import org.springframework.data.jpa.domain.Specification;

public interface UsuarioSpec {

  Specification<Usuario> filter(final UsuarioFilter filter);

  Specification<Usuario> filterSesionActiva(final UsuarioFilter filter);

}
