package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import java.util.List;
import java.util.Optional;

public interface ArchivoRepository extends JpaRepository<Archivo, Long> {

    List<Archivo> findByNombre(String nombre);

    List<Archivo> findByGuidAndActivo(String guid, Integer activo);

    List<Archivo> findByNombreAndActivo(String nombre, Integer activo);
    
    Optional<Archivo> findByGuid(String guid);

    @Modifying
    @Query("DELETE FROM Archivo")
    void deleteAllInBatch();

    long countByGuidStartingWithAndActivo(String guidPrefix, Integer activo);

    @Modifying
    @Query(value = "DELETE FROM tab_archivo WHERE n_archivo_pk IN (SELECT n_archivo_pk FROM tab_archivo LIMIT :batchSize)", nativeQuery = true)
    int deleteInBatch(@Param("batchSize") int batchSize);


}
