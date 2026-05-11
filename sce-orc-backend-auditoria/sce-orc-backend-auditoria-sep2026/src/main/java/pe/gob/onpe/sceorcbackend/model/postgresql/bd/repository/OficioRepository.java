package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Oficio;

public interface OficioRepository extends JpaRepository<Oficio, Integer> {

    List<Oficio> findByCentroComputo(Integer centroComputo);

    Optional<Oficio> findByNombreOficio(String nombreOficio);

    @Modifying
    @Query("DELETE FROM Oficio")
    void deleteAllInBatch();

}
