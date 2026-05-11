package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MiembroMesaEscrutinio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface MiembroMesaEscrutinioRepository extends JpaRepository<MiembroMesaEscrutinio, Long>,
    JpaSpecificationExecutor<MiembroMesaEscrutinio> {

	@Query("SELECT mme FROM MiembroMesaEscrutinio mme "
			+ "JOIN mme.mesa m "
			+ "WHERE m.id = ?1 ")
	List<MiembroMesaEscrutinio> findByMesaId(Long idMesa);


	@Modifying
	@Query("DELETE FROM MiembroMesaEscrutinio")
	void deleteAllInBatch();


	@Query("SELECT COUNT(mme) > 0 FROM MiembroMesaEscrutinio mme " +
			"WHERE mme.documentoIdentidadPresidente = ?1 OR " +
			"mme.documentoIdentidadSecretario = ?1 OR " +
			"mme.documentoIdentidadTercerMiembro = ?1")
	boolean existeDniRegistrado(String dni);


	@Query("SELECT COUNT(p) FROM MiembroMesaEscrutinio p WHERE p.activo = :activo")
	Long contarPorActivo(@Param("activo") Integer activo);

}
