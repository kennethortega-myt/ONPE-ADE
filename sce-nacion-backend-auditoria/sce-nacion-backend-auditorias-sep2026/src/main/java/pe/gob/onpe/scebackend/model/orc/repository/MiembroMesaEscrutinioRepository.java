package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.MiembroMesaEscrutinio;

public interface MiembroMesaEscrutinioRepository extends JpaRepository<MiembroMesaEscrutinio, Long> {

  Optional<MiembroMesaEscrutinio> findByIdCc(String cc);


 @Query(value = """
             SELECT
          m.c_mesa,
          e.c_documento_identidad_presidente AS c_documento_identidad,
          v.n_ubigeo AS c_ubigeo,
          presidente_padron.c_nombres AS c_nombres,
          presidente_padron.c_apellido_paterno AS c_apellido_paterno,
          presidente_padron.c_apellido_materno AS c_apellido_materno,
          presidente_padron.n_tipo_documento_identidad AS n_tipo_documento_identidad,
                (SELECT
              n_codigo
            FROM det_catalogo_estructura
            WHERE n_activo = 1
              AND n_catalogo IN (
                SELECT n_catalogo_pk
                FROM cab_catalogo
                WHERE c_maestro = 'mae_cargo_miembro_mesa'
                  AND n_activo = 1
              )
              AND c_nombre = 'Presidente'
            )
      FROM
          tab_miembro_mesa_escrutinio e
      INNER JOIN tab_mesa m ON e.n_mesa = CAST(m.c_mesa AS INTEGER)
      INNER JOIN mae_local_votacion v ON m.n_local_votacion = v.n_local_votacion_pk
      INNER JOIN mae_ubigeo u ON v.n_ubigeo = u.n_ubigeo_pk
      LEFT JOIN tab_padron_electoral presidente_padron ON e.c_documento_identidad_presidente = presidente_padron.c_documento_identidad AND e.n_mesa = CAST(presidente_padron.c_mesa AS INTEGER)
      WHERE
          (e.n_mesa = :numeroMesa OR :numeroMesa IS NULL)
          AND (e.c_documento_identidad_presidente IS NOT NULL)
          AND (:dni IS NULL OR e.c_documento_identidad_presidente = CAST(:dni AS VARCHAR))

      UNION ALL

      SELECT
          m.c_mesa,
          e.c_documento_identidad_secretario AS c_documento_identidad,
          v.n_ubigeo AS c_ubigeo,
          secretario_padron.c_nombres AS c_nombres,
          secretario_padron.c_apellido_paterno AS c_apellido_paterno,
          secretario_padron.c_apellido_materno AS c_apellido_materno,
          secretario_padron.n_tipo_documento_identidad AS n_tipo_documento_identidad,
                (SELECT
              n_codigo
            FROM det_catalogo_estructura
            WHERE n_activo = 1
              AND n_catalogo IN (
                SELECT n_catalogo_pk
                FROM cab_catalogo
                WHERE c_maestro = 'mae_cargo_miembro_mesa'
                  AND n_activo = 1
              )
              AND c_nombre = 'Secretario'
            )
      FROM
          tab_miembro_mesa_escrutinio e
      INNER JOIN tab_mesa m ON e.n_mesa = CAST(m.c_mesa AS INTEGER)
      INNER JOIN mae_local_votacion v ON m.n_local_votacion = v.n_local_votacion_pk
      INNER JOIN mae_ubigeo u ON v.n_ubigeo = u.n_ubigeo_pk
      LEFT JOIN tab_padron_electoral secretario_padron ON e.c_documento_identidad_secretario = secretario_padron.c_documento_identidad AND e.n_mesa = CAST(secretario_padron.c_mesa AS INTEGER)
      WHERE
          (e.n_mesa = :numeroMesa OR :numeroMesa IS NULL)
          AND (e.c_documento_identidad_secretario IS NOT NULL)
          AND (:dni IS NULL OR e.c_documento_identidad_secretario = CAST(:dni AS VARCHAR))

      UNION ALL

      SELECT
          m.c_mesa,
          e.c_documento_identidad_tercer_miembro AS c_documento_identidad,
          v.n_ubigeo AS c_ubigeo,
          tercer_miembro_padron.c_nombres AS c_nombres,
          tercer_miembro_padron.c_apellido_paterno AS c_apellido_paterno,
          tercer_miembro_padron.c_apellido_materno AS c_apellido_materno,
          tercer_miembro_padron.n_tipo_documento_identidad AS n_tipo_documento_identidad,
                (SELECT
              n_codigo
            FROM det_catalogo_estructura
            WHERE n_activo = 1
              AND n_catalogo IN (
                SELECT n_catalogo_pk
                FROM cab_catalogo
                WHERE c_maestro = 'mae_cargo_miembro_mesa'
                  AND n_activo = 1
              )
              AND c_nombre = 'Tercer Miembro'
            )
      FROM
          tab_miembro_mesa_escrutinio e
      INNER JOIN tab_mesa m ON e.n_mesa = CAST(m.c_mesa AS INTEGER)
      INNER JOIN mae_local_votacion v ON m.n_local_votacion = v.n_local_votacion_pk
      INNER JOIN mae_ubigeo u ON v.n_ubigeo = u.n_ubigeo_pk
      LEFT JOIN tab_padron_electoral tercer_miembro_padron ON e.c_documento_identidad_tercer_miembro = tercer_miembro_padron.c_documento_identidad AND e.n_mesa = CAST(tercer_miembro_padron.c_mesa AS INTEGER)
      WHERE
          (e.n_mesa = :numeroMesa OR :numeroMesa IS NULL)
          AND (e.c_documento_identidad_tercer_miembro IS NOT NULL)
          AND (:dni IS NULL OR e.c_documento_identidad_tercer_miembro = CAST(:dni AS VARCHAR))
      ORDER BY
          c_mesa ASC
            """, nativeQuery = true)
  List<Object[]> findMiembrosMesa(
      @Param("numeroMesa") Integer numeroMesa,
      @Param("dni") String dni);

  @Query(value = "SELECT det.ubigeo.id " +
               "FROM UbigeoEleccion det " +
               "JOIN det.eleccion e " +
               "JOIN e.procesoElectoral cpe " +
               "WHERE e.principal = 1 " +
               "AND cpe.acronimo = :acronimo")
  List<Integer> findUbigeoByAcronimo(@Param("acronimo") String acronimo);

   @Query(value = """
             SELECT	n_centro_computo, c_codigo_centro_computo, c_nombre_centro_computo,
						d_fecha_ultima_modificacion, n_mesas_habiles,
						n_actas_ingresadas, n_actas_procesadas, n_actas_contabilizadas, n_actas_observadas,		
						COALESCE(ROUND((n_actas_procesadas / NULLIF(n_mesas_habiles, 0)) * 100, 3), 0.000) AS n_porcentaje_actas_procesadas,
						COALESCE(ROUND((n_actas_contabilizadas / NULLIF(n_mesas_habiles, 0)) * 100, 3), 0.000) AS n_porcentaje_actas_contabilizadas,
						COALESCE(ROUND((n_actas_observadas / NULLIF(n_mesas_habiles, 0)) * 100, 3), 0.000) AS n_porcentaje_actas_observadas,
						CASE WHEN n_mesas_habiles=n_actas_procesadas THEN 1 ELSE 0 END n_flag_procesadas_completas,
						CASE WHEN n_mesas_habiles=n_actas_contabilizadas THEN 1 ELSE 0 END n_flag_contabilizadas_completas
				FROM 
					(SELECT	vu.n_centro_computo, vu.c_codigo_centro_computo, vu.c_nombre_centro_computo,
							MAX(ca.d_aud_fecha_modificacion) d_fecha_ultima_modificacion,
							COUNT(ca.n_acta_pk) n_mesas_habiles,
							COALESCE(SUM(CASE WHEN ca.c_estado_acta<>'A' THEN 1 END),0) n_actas_ingresadas,
							COALESCE(SUM(CASE WHEN (ca.c_estado_acta >'G' OR ca.c_estado_acta='D') AND ca.c_estado_acta<>'T' THEN 1 END),0) n_actas_procesadas,
							COALESCE(SUM(CASE WHEN ca.c_estado_computo = 'S'  THEN 1 END),0) n_actas_contabilizadas,
							COALESCE(SUM(CASE WHEN ca.c_estado_computo = 'O' OR ca.c_estado_computo = 'E' OR ca.c_estado_acta_resolucion='R' THEN 1 END),0) n_actas_observadas
					FROM	vw_ubigeo vu
						INNER JOIN	det_ubigeo_eleccion due ON vu.n_ubigeo_pk=due.n_ubigeo AND due.n_activo=1
						INNER JOIN	cab_acta ca ON due.n_det_ubigeo_eleccion_pk=ca.n_det_ubigeo_eleccion AND ca.n_activo=1
					WHERE 	due.n_eleccion = :tipoEleccion
					GROUP BY	vu.n_centro_computo, vu.c_codigo_centro_computo, vu.c_nombre_centro_computo
				ORDER BY	vu.n_centro_computo) Q1
            """, nativeQuery = true)
      List<Map<String, Object>> obtenerActasContabilizadas(
            @Param("tipoEleccion") Integer tipoEleccion);


      @Query("SELECT e.id FROM Eleccion e WHERE e.principal = 1 AND e.activo = 1")
      Optional<Integer> obtenerTipoEleccion();
      
}
