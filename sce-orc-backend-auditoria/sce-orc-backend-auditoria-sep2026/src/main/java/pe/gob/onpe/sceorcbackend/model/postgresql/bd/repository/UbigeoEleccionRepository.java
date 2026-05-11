package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.dto.EleccionDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Ubigeo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.UbigeoEleccion;

import java.util.List;

public interface UbigeoEleccionRepository extends JpaRepository<UbigeoEleccion, Long> {


    UbigeoEleccion findByUbigeoAndEleccion(Ubigeo ubigeo, Eleccion eleccion);

    List<UbigeoEleccion> findByEleccion(Eleccion eleccion);
    
    @Modifying
    @Query("DELETE FROM UbigeoEleccion")
    void deleteAllInBatch();

    @Query("SELECT new pe.gob.onpe.sceorcbackend.model.dto.EleccionDto(e.id, e.codigo, e.nombre) " +
        "FROM UbigeoEleccion ue " +
        "JOIN ue.eleccion e " +
        "WHERE ue.ubigeo.id = :ubigeoId " +
        "AND ue.activo = 1 AND e.activo = 1 order by e.codigo")
    List<EleccionDto> findEleccionesByUbigeoId(@Param("ubigeoId") Long ubigeoId);

}
