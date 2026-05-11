package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOficio;

public interface DetActaOficioRepository extends JpaRepository<DetActaOficio, Integer> {

    Optional<DetActaOficio> findByActa_Id(Long idActa);

    List<DetActaOficio> findByOficio_Id(Integer oficioId);

    List<DetActaOficio> findByActaCeleste_Id(Long idActaCeleste);

    Optional<DetActaOficio> findFirstByActa_IdOrderByFechaCreacionDesc(Long actaId);

    @Modifying
    @Query("DELETE FROM DetActaOficio")
    void deleteAllInBatch();

}
