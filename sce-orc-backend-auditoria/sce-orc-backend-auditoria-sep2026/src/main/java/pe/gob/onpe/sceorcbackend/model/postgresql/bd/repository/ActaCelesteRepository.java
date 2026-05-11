package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaCeleste;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.ActaCelesteProjection;
import pe.gob.onpe.sceorcbackend.model.postgresql.projection.ActaScanProjection;

public interface ActaCelesteRepository extends JpaRepository<ActaCeleste, Long> {

	Optional<ActaCeleste> findByActa_Id(Long actaId);
	
	Optional<ActaCeleste> findByActa_IdAndEstadoDigitalizacion(Long actaId, String estadoDigitalizacion);

	@Query("SELECT SUM(CASE WHEN a.estadoDigitalizacion = 'D' THEN 1 ELSE 0 END) AS pending, "
			+ "SUM(CASE WHEN a.estadoDigitalizacion = 'C' THEN 1 ELSE 0 END) AS approved, "
			+ "SUM(CASE WHEN a.estadoDigitalizacion = 'O' THEN 1 ELSE 0 END) AS rejected " 
			+ "FROM ActaCeleste a "
			+ "WHERE a.acta.ubigeoEleccion.eleccion.codigo = :codigoEleccion ")
	Object[] getDigitalizationSummary(@Param("codigoEleccion") String codigoEleccion);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<ActaCeleste> findByEstadoDigitalizacionAndUsuarioAsignadoIsNull(String cEstadoDigitalizacion);
	
	List<ActaCeleste> findByEstadoDigitalizacionAndUsuarioAsignado(String cEstadoDigitalizacion, String usuario);
	
	@Modifying
    @Query("DELETE FROM ActaCeleste")
    void deleteAllInBatch();





	/**
	 * metodos para liberar actas en control de digitalización
	 */
	@Query(""" 
        SELECT a.id AS id,
               a.usuarioAsignado AS usuarioAsignado
        FROM ActaCeleste a
        WHERE a.estadoDigitalizacion = :estadoDigitalizacion
          AND a.usuarioAsignado IS NOT NULL
    """)
	List<ActaCelesteProjection> buscarActaCelesteControlDigitalizacionTomadas(@Param("estadoDigitalizacion") String estadoDigitalizacion);


	@Transactional
	@Modifying
	@Query("""
       UPDATE ActaCeleste a
       SET
         a.usuarioAsignado  = null,
         a.asignado = 0
       WHERE a.id = :id
       """)
	void liberarActasCelesteControlDigitalizacion(@Param("id") Long id);

	@Query(value = """
    SELECT
        actaCeleste.id AS idActa,
        e.nombre AS nombreEleccion,
        e.codigo AS codigoEleccion,
        m.codigo AS mesa,
        COALESCE(actaCeleste.numeroCopia, '') AS copia,
        COALESCE(actaCeleste.digitoChequeoEscrutinio, '') AS digitoChequeoEscrutinio,
        acta.estadoActa AS estadoActa,
        acta.estadoCc AS estadoComputo,
        actaCeleste.estadoDigitalizacion AS estadoDigitalizacion,
        COALESCE(ae.nombre, '') AS archivoEscrutinio,
        COALESCE(ai.nombre, '') AS archivoInstalacion,
        COALESCE(asuf.nombre, '') AS archivoSufragio,
        COALESCE(ais.nombre, '') AS archivoInstalacionSufragio,
        actaCeleste.activo AS activo,
        acta.solucionTecnologica AS solucionTecnologica,
        acta.tipoTransmision AS tipoTransmision,
        actaCeleste.fechaModificacion AS fechaModificacion
    FROM ActaCeleste actaCeleste
    JOIN actaCeleste.acta acta
    JOIN acta.mesa m
    JOIN acta.ubigeoEleccion ue
    JOIN ue.eleccion e
    LEFT JOIN actaCeleste.archivoEscrutinio ae
    LEFT JOIN actaCeleste.archivoInstalacion ai
    LEFT JOIN actaCeleste.archivoSufragio asuf
    LEFT JOIN actaCeleste.archivoInstalacionSufragio ais
    WHERE (:codigoEleccion IS NULL OR :codigoEleccion = '' OR e.codigo = :codigoEleccion)
    AND actaCeleste.estadoDigitalizacion IN :estadosDigitalizacion order by actaCeleste.fechaModificacion desc
    """)
	List<ActaScanProjection> findActasCelesteSceScanenr(@Param("codigoEleccion") String codigoEleccion,
												 @Param("estadosDigitalizacion") List<String> estadosDigitalizacion);
}
