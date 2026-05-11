package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.Personero;

public interface PersoneroRepository extends JpaRepository<Personero, Long> {

	Optional<Personero> findByDocumentoIdentidad(String documentoIdentidad);
	
	@Query("SELECT p FROM Personero p "
			+ "JOIN  p.mesa m "
			+ "WHERE p.documentoIdentidad = ?1 and m.id= ?2")
	Optional<Personero> findByDocumentoIdentidadAndIdMesa(String documentoIdentidad, Long idMesa);
	
}
