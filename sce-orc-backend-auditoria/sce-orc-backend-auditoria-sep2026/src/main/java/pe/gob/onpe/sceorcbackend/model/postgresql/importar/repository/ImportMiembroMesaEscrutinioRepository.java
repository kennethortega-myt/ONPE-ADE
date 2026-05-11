package pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository;


import org.springframework.data.jpa.repository.Modifying;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportMiembroMesaEscrutinio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImportMiembroMesaEscrutinioRepository extends JpaRepository<ImportMiembroMesaEscrutinio, Long> {

	@Modifying
	@Query("DELETE FROM ImportMiembroMesaEscrutinio")
	void deleteAllInBatch();


}
