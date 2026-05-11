package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Ubigeo;

import java.util.List;
import java.util.Optional;

public interface UbigeoRepository extends JpaRepository<Ubigeo, Long> {

	Optional<Ubigeo> findFirstByCentroComputo(CentroComputo centroComputo);


	List<Ubigeo> findByUbigeoPadre_Id(Long idPadre);

	@Modifying
	@Query("DELETE FROM Ubigeo")
	void deleteAllInBatch();

	@Query(value = """
        WITH RECURSIVE ubigeo_hierarchy AS (
            SELECT 
                n_ubigeo_pk, 
                n_ubigeo_padre, 
                c_ubigeo, 
                c_nombre, 
                c_provincia, 
                c_departamento
            FROM mae_ubigeo 
            WHERE c_ubigeo =:codigoUbigeo 

            UNION ALL

            SELECT 
                u.n_ubigeo_pk, 
                u.n_ubigeo_padre, 
                u.c_ubigeo, 
                u.c_nombre, 
                u.c_provincia, 
                u.c_departamento
            FROM mae_ubigeo u
            INNER JOIN ubigeo_hierarchy uh ON u.n_ubigeo_pk = uh.n_ubigeo_padre
        )
        SELECT 
            uh1.c_ubigeo AS c_ubigeo,
            uh1.c_nombre AS distrito,
            uh2.c_nombre AS provincia,
            uh3.c_nombre AS departamento
        FROM ubigeo_hierarchy uh1
        LEFT JOIN ubigeo_hierarchy uh2 ON uh1.n_ubigeo_padre = uh2.n_ubigeo_pk
        LEFT JOIN ubigeo_hierarchy uh3 ON uh2.n_ubigeo_padre = uh3.n_ubigeo_pk
        WHERE uh1.c_ubigeo =:codigoUbigeo
        """, nativeQuery = true)
	List<Object[]> getUbigeoGeneral(@Param("codigoUbigeo") String codigoUbigeo);



	@Query(value = """
    SELECT ae.c_codigo 
    FROM mae_ubigeo u
    JOIN mae_centro_computo cc ON u.n_centro_computo = cc.n_centro_computo_pk
    JOIN mae_ambito_electoral ae ON u.n_ambito_electoral = ae.n_ambito_electoral_pk
    WHERE cc.c_codigo = :codigoCentroComputo
    LIMIT 1
    """, nativeQuery = true)
	Optional<String> findCodigoAmbitoByCodigoCentroComputo(@Param("codigoCentroComputo") String codigoCentroComputo);


}
