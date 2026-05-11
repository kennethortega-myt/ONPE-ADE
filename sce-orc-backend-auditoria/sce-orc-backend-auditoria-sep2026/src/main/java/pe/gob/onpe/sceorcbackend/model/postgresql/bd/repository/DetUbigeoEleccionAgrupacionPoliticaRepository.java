package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AgrupacionPolitica;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetUbigeoEleccionAgrupacionPolitica;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.UbigeoEleccion;

import java.util.List;
import java.util.Optional;

public interface DetUbigeoEleccionAgrupacionPoliticaRepository extends JpaRepository<DetUbigeoEleccionAgrupacionPolitica, Long> {

    List<DetUbigeoEleccionAgrupacionPolitica> findByUbigeoEleccion(UbigeoEleccion ubigeoEleccion);
    List<DetUbigeoEleccionAgrupacionPolitica> findByUbigeoEleccionId(Long ubigeoEleccionId);
    Optional<DetUbigeoEleccionAgrupacionPolitica> findByPosicionAndUbigeoEleccionId(Integer posicion, Long idUbigeoEleccion);
    DetUbigeoEleccionAgrupacionPolitica findByUbigeoEleccionAndAgrupacionPolitica (UbigeoEleccion UbigeoEleccion, AgrupacionPolitica agrupacionPolitica);

    @Modifying
    @Query("DELETE FROM DetUbigeoEleccionAgrupacionPolitica")
    void deleteAllInBatch();
    
}
