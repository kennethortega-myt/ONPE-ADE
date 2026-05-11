package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.Ubigeo;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

public interface UbigeoRepository extends JpaRepository<Ubigeo, Long>, MigracionRepository<Ubigeo, String> {

	@Query(value = """
			WITH RECURSIVE ubigeos_hijos AS (
                SELECT ub.*
                FROM mae_ubigeo ub
                INNER JOIN mae_centro_computo cc 
                ON cc.n_centro_computo_pk = ub.n_centro_computo
                WHERE cc.c_codigo = ?1
                UNION ALL
                SELECT padre.*
                FROM mae_ubigeo padre
                INNER JOIN ubigeos_hijos hijo 
                ON hijo.n_ubigeo_padre = padre.n_ubigeo_pk
            )
            select distinct * 
            FROM ubigeos_hijos 
            ORDER BY n_ubigeo_padre NULLS first
        """, nativeQuery = true)
	public List<Ubigeo> findByCc(String codigo);
	
}
