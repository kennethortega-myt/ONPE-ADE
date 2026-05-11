package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.VwActaMonitoreo;

public interface VwActaMonitoreoRepository extends JpaRepository<VwActaMonitoreo, Long> {

	@Query(value = """
		    SELECT a.* 
		    FROM vw_acta_monitoreo a
		    WHERE 1=1 
		    AND (:idProceso IS NULL OR a.n_proceso_electoral = :idProceso) 
		    AND (:idEleccion IS NULL OR a.n_eleccion = :idEleccion) 
		    AND (:idDepartamento IS NULL OR a.n_ubigeo_nivel_3 = :idDepartamento) 
		    AND (:idProvincia IS NULL OR a.n_ubigeo_nivel_2 = :idProvincia) 
		    AND (:idUbigeo IS NULL OR a.n_ubigeo_nivel_1 = :idUbigeo) 
		    AND (:idLocalVotacion IS NULL OR a.n_local_votacion = :idLocalVotacion) 
		    AND (:mesa IS NULL OR a.c_mesa = :mesa) 
		    AND (:grupoActa IS NULL OR a.c_grupo_acta = :grupoActa) 
		    ORDER BY a.n_acta_pk
		""",
		nativeQuery = true)
			List<VwActaMonitoreo> buscarMonitoreoNacion(
					@Param("idProceso") Long idProceso, 
					@Param("idEleccion") Long idEleccion, 
					@Param("idDepartamento") Long idDepartamento, 
					@Param("idProvincia") Long idProvincia, 
					@Param("idUbigeo") Long idUbigeo, 
					@Param("idLocalVotacion") Long idLocalVotacion,
					@Param("mesa") String mesa,
					@Param("grupoActa") String grupoActa,
					Pageable pageable);
	
	@Query(value = """
		    SELECT count(a.n_acta_pk) 
		    FROM vw_acta_monitoreo a
		    WHERE 1=1 
		    AND (:idProceso IS NULL OR a.n_proceso_electoral = :idProceso) 
		    AND (:idEleccion IS NULL OR a.n_eleccion = :idEleccion) 
		    AND (:idDepartamento IS NULL OR a.n_ubigeo_nivel_3 = :idDepartamento) 
		    AND (:idProvincia IS NULL OR a.n_ubigeo_nivel_2 = :idProvincia) 
		    AND (:idUbigeo IS NULL OR a.n_ubigeo_nivel_1 = :idUbigeo) 
		    AND (:idLocalVotacion IS NULL OR a.n_local_votacion = :idLocalVotacion) 
		    AND (:mesa IS NULL OR a.c_mesa = :mesa) 
		    AND (:grupoActa IS NULL OR a.c_grupo_acta = :grupoActa) 
		""",
		nativeQuery = true)
			Integer getCantidadRegistros(
					@Param("idProceso") Long idProceso, 
					@Param("idEleccion") Long idEleccion, 
					@Param("idDepartamento") Long idDepartamento, 
					@Param("idProvincia") Long idProvincia, 
					@Param("idUbigeo") Long idUbigeo, 
					@Param("idLocalVotacion") Long idLocalVotacion,
					@Param("mesa") String mesa,
					@Param("grupoActa") String grupoActa);
}
