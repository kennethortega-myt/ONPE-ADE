package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.scebackend.model.orc.entities.Oficio;

public interface OficioRepository extends JpaRepository<Oficio, Integer> {

    Optional<Oficio> findByIdCc(String cc);

    Optional<Oficio> findByNombreOficio(String nombreOficio);
}
