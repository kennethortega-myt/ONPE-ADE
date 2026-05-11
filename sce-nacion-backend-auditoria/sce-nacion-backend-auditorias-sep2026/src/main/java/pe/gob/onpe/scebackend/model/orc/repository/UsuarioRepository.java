package pe.gob.onpe.scebackend.model.orc.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.gob.onpe.scebackend.model.orc.entities.Usuario;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

import java.util.List;


public interface UsuarioRepository extends JpaRepository<Usuario, Long>, MigracionRepository<Usuario, String> {
	@Query("SELECT u FROM Usuario u WHERE u.centroComputo  = ?1")
	public List<Usuario> findByCc(String codigo);
	
	Usuario findByUsuario(String usuario);
}
