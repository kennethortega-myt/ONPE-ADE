package pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportPadronElectoral;


public interface ImportPadronElectoralRepository extends JpaRepository<ImportPadronElectoral, Long> {

	List<ImportPadronElectoral> findPadronElectoralByCodigoMesaOrderByOrden(String codigoMesa);
	
	@Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE tab_padron_electoral", nativeQuery = true)
    void truncateTable();
	
}
