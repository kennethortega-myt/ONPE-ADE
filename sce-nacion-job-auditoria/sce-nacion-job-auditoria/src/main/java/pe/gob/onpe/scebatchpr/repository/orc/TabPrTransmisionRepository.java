package pe.gob.onpe.scebatchpr.repository.orc;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebatchpr.entities.orc.TabPrTransmision;


public interface TabPrTransmisionRepository extends JpaRepository<TabPrTransmision, Long> {
	

	@Transactional(readOnly = true)
	@Query("SELECT t FROM TabPrTransmision t WHERE t.estado = 0 and t.activo=1 and t.nombreVista='vw_pr_acta' ORDER BY id asc ")
	public List<TabPrTransmision> listarPendientes(Pageable pageable);
	
	@Modifying
	@Query("UPDATE TabPrTransmision t SET t.estado = :newState WHERE t.id IN :idList")
	int actualizarEstados(@Param("idList") List<Long> idList, @Param("newState") Integer newState);
	
	@Modifying
	@Query("UPDATE TabPrTransmision t SET t.estado = :newState, t.mensaje=:mensaje,t.enviado=0 WHERE t.id = :idTransferencia")
	int actualizarEstado(@Param("idTransferencia") Long idTransferencia, @Param("newState") Integer newState, @Param("mensaje") String mensajae);
	
	@Modifying
	@Query("UPDATE TabPrTransmision t SET t.correlativo = :correlativo WHERE t.id = :idTransferencia")
	int actualizarCorrelativo(@Param("idTransferencia") Long idTransferencia, @Param("correlativo") String correlativo);
	
	@Modifying
	@Query("UPDATE TabPrTransmision t SET t.enviado = :newState WHERE t.correlativo = :correlativo")
	int actualizarEnviadoPorCorrelativo(@Param("correlativo") String correlativo, @Param("newState") Integer newState);

}
