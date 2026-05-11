package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.AgrupacionPoliticaReal;

public interface AgrupacionPoliticaRealRepository extends JpaRepository<AgrupacionPoliticaReal, Long> {

	
	@Query(value = """
			SELECT distinct a.* FROM mae_agrupacion_politica_real a 
			INNER JOIN det_ubigeo_eleccion_agrupacion_politica dueap on a.n_agrupacion_politica_real_pk = dueap.n_agrupacion_politica 
			INNER JOIN det_ubigeo_eleccion due ON due.n_det_ubigeo_eleccion_pk = dueap.n_det_ubigeo_eleccion 
			INNER JOIN mae_ubigeo mu ON mu.n_ubigeo_pk = due.n_ubigeo 
			INNER JOIN mae_centro_computo mcc on mcc.n_centro_computo_pk = mu.n_centro_computo 
			WHERE mcc.c_codigo = ?1
			""", nativeQuery = true)
	List<AgrupacionPoliticaReal> findByCc(String cc);
	
}
