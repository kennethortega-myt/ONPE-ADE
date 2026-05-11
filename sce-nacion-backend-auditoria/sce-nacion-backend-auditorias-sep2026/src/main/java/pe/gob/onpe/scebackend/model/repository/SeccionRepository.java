package pe.gob.onpe.scebackend.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.entities.Seccion;

import java.util.List;

public interface SeccionRepository extends JpaRepository<Seccion, Integer>, MigracionRepository<Seccion, String> {

    @Modifying
    @Query("update Seccion sec set sec.activo = :status where sec.id = :id")
    void updateEstado(@Param("status") Integer status, @Param("id") Integer id);
    
    List<Seccion> findByActivo(Integer activo);
    
    @Query("SELECT s FROM Seccion s")
    List<Seccion> findByCc(String acronimo);
}
