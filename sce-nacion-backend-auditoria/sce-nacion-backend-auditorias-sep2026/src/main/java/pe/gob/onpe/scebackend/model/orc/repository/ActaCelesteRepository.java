package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.scebackend.model.orc.entities.ActaCeleste;


public interface ActaCelesteRepository extends JpaRepository<ActaCeleste, Long> {

	Optional<ActaCeleste> findByActa_Id(Long actaId);
	Optional<ActaCeleste> findByIdCc(String idCc);

}
