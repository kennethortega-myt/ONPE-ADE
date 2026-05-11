package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.scebackend.model.orc.entities.DetActaOficio;

public interface DetActaOficioRepository extends JpaRepository<DetActaOficio, Integer> {

    Optional<DetActaOficio> findByIdCc(String idCc);

    List<DetActaOficio> findByOficioId(Integer oficioId);
}
