package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.gob.onpe.scebackend.model.orc.entities.AmbitoElectoral;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Repository
public interface AmbitoElectoralRepository
        extends JpaRepository<AmbitoElectoral, Long>, MigracionRepository<AmbitoElectoral, String> {

    @Query(value = """
            WITH RECURSIVE ambitos_electorales_hijos AS (
                SELECT am.*
                FROM mae_ambito_electoral am
                INNER JOIN mae_ubigeo u ON am.n_ambito_electoral_pk = u.n_ambito_electoral
                INNER JOIN mae_centro_computo cc ON cc.n_centro_computo_pk = u.n_centro_computo
                WHERE cc.c_codigo = ?1
                UNION ALL
                SELECT padre.*
                FROM mae_ambito_electoral padre
                INNER JOIN ambitos_electorales_hijos hijo
                    ON hijo.n_ambito_electoral_padre = padre.n_ambito_electoral_pk
            )
            SELECT DISTINCT *
            FROM ambitos_electorales_hijos
            ORDER BY n_ambito_electoral_padre NULLS FIRST
            """, nativeQuery = true)
    public List<AmbitoElectoral> findByCc(String codigo);

    AmbitoElectoral findByAmbitoElectoralPadreIsNull();

    @Query(value = "SELECT * FROM fn_obtener_datos_ambito_electoral_x_centro_computo(:c_esquema, :n_centro_computo)", nativeQuery = true)
    List<Map<String, Object>> listarAmbitoElectoralPorCentroComputo(@Param("c_esquema") String piEsquema,
            @Param("n_centro_computo") Integer idCentroComputo);

    @Query(value = "SELECT * FROM fn_obtener_datos_ambito_electoral_x_eleccion(:c_esquema,:n_eleccion)", nativeQuery = true)
    List<Map<String, Object>> listarAmbitoElectoralPorEleccion(@Param("c_esquema") String piEsquema,
            @Param("n_eleccion") Integer idEleccion);
}
