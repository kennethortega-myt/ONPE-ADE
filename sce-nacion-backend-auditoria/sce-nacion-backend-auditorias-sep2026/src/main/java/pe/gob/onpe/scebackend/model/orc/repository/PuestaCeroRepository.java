package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import pe.gob.onpe.scebackend.model.orc.entities.PuestaCero;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

public interface PuestaCeroRepository extends JpaRepository<PuestaCero, Long>, MigracionRepository<PuestaCero, String>{

	@Query("SELECT distinct p FROM PuestaCero p JOIN p.centroComputo c WHERE c.codigo = ?1")
	List<PuestaCero> findByCc(String codigo);

	@Query(value = "select * from fn_reporte_puesta_cero_digitalizacion(:piEsquema,:piAudUsuarioConsulta)", nativeQuery = true)
	List<Map<String, Object>> reportePuestaCeroDigitalizacion(@Param("piEsquema") String piEsquema,
												   @Param("piAudUsuarioConsulta") String piAudUsuarioConsulta);

	@Query(value = "select * from fn_reporte_puesta_cero_digitacion(:piEsquema,:piAudUsuarioConsulta)", nativeQuery = true)
	List<Map<String, Object>> reportePuestaCeroDigitacion(@Param("piEsquema") String piEsquema,
															  @Param("piAudUsuarioConsulta") String piAudUsuarioConsulta);

	@Query(value = "select * from fn_reporte_puesta_cero_otros(:piEsquema,:piAudUsuarioConsulta)", nativeQuery = true)
	List<Map<String, Object>> reportePuestaCeroOmisos(@Param("piEsquema") String piEsquema,
															  @Param("piAudUsuarioConsulta") String piAudUsuarioConsulta);
	
}
