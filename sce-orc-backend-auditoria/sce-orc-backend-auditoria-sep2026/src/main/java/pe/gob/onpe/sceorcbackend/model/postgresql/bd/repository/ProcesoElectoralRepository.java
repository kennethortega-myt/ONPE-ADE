package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ProcesoElectoral;

import java.util.Optional;

public interface ProcesoElectoralRepository extends JpaRepository<ProcesoElectoral, Long> {

    ProcesoElectoral findByActivo(Integer activo);
    Optional<ProcesoElectoral> findById(Long id);
    ProcesoElectoral findByAcronimo(String acronimo);
    
    @Modifying
    @Query("DELETE FROM ProcesoElectoral p")
    void deleteAllInBatch();
    
    @Query("SELECT COUNT(p) FROM ProcesoElectoral p")
    long contarTodos();

}
