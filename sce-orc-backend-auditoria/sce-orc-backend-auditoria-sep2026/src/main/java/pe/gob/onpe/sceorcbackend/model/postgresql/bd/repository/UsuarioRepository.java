package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

	Usuario findByUsuario(String usuername);

	@Modifying
	@Query(value = "UPDATE tab_usuario SET b_desincronizado_sasa = :desincronizadoSasa WHERE n_usuario_pk = :usuarioPk", nativeQuery = true)
	int updateDesincronizado(@Param("usuarioPk") Long usuarioPk,
			@Param("desincronizadoSasa") Boolean desincronizadoSasa);

	List<Usuario> findBySesionActiva(Integer activo);
	long countBySesionActivaAndUsuarioNot(Integer sesionActiva, String usuario);
  
  	@Modifying
	@Query("DELETE FROM Usuario")
	void deleteAllInBatch();

    List<Usuario> findByPerfil(String perfil);


}
