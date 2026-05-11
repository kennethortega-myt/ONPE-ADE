package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Personero;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PersoneroRepository  extends JpaRepository<Personero, Long>, JpaSpecificationExecutor<Personero> {

	@Query("SELECT p FROM Personero p "
	    		+ "JOIN p.mesa m "
	    		+ "WHERE m.id = ?1 ")
	List<Personero> findByIdMesa(Long idMesa);


	@Modifying
	@Query("DELETE FROM Personero")
	void deleteAllInBatch();

	@Query("SELECT COUNT(p) > 0 FROM Personero p " +
			"WHERE p.documentoIdentidad = ?1 ")
	boolean existeDniRegistrado(String dni);

	@Modifying
	@Query("UPDATE Personero p SET p.activo = :activo, p.usuarioModificacion = :usuario, p.fechaModificacion = CURRENT_TIMESTAMP WHERE p.id = :id")
	int actualizarPersonero( @Param("activo") Integer activo, @Param("usuario") String usuario, @Param("id") Long id);


	@Query("SELECT COUNT(p) FROM Personero p WHERE p.activo = :activo")
	Long contarPorActivo(@Param("activo") Integer activo);
}
