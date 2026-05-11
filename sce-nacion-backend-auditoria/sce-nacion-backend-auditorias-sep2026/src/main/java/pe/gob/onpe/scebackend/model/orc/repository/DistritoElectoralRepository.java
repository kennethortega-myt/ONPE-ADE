package pe.gob.onpe.scebackend.model.orc.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.DistritoElectoral;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

public interface DistritoElectoralRepository extends JpaRepository<DistritoElectoral, Integer>, MigracionRepository<DistritoElectoral, String> {

	@Query(value = """
            WITH RECURSIVE distritos_hijos AS (
                SELECT *
                FROM mae_distrito_electoral 
                UNION ALL
                SELECT hijo.*
                FROM mae_distrito_electoral hijo
                INNER JOIN distritos_hijos padre ON hijo.n_distrito_electoral_padre = padre.n_distrito_electoral_pk
                INNER JOIN mae_ubigeo u ON hijo.n_distrito_electoral_pk = u.n_distrito_electoral
                INNER JOIN mae_centro_computo cc ON cc.n_centro_computo_pk = u.n_centro_computo
                WHERE cc.c_codigo = ?1
            )
            SELECT * 
            FROM distritos_hijos 
            ORDER BY n_distrito_electoral_padre NULLS FIRST
            """, nativeQuery = true)
	public List<DistritoElectoral> findByCc(String codigo);

    public DistritoElectoral findByNombre(String nombre);

    @Query(value = "SELECT * FROM fn_cifra_distrito_electoral_x_eleccion(:pi_esquema, :pi_n_eleccion)", nativeQuery = true)
    public List<Map<String, Object>> obtenerDistritoElectoralByEleccion(
        @Param("pi_esquema") String esquema,
        @Param("pi_n_eleccion") String codEleccion
    );

    @Query(value = "SELECT * FROM fn_cifra_variables_sistema(:pi_esquema)", nativeQuery = true)
    public List<Map<String, Object>> obtenerVariablesSistema(
        @Param("pi_esquema") String esquema
    );

    @Query(value = "SELECT * FROM fn_cifra_reporte_resultados(:pi_esquema, :pi_tipo_eleccion,:pi_distrito_electoral, :pi_estado_cifra, :pi_tipo_cifra)", nativeQuery = true)
    public List<Map<String, Object>> obtenerReporteResultados(
        @Param("pi_esquema") String esquema,
        @Param("pi_tipo_eleccion") String tipoEleccion,
        @Param("pi_distrito_electoral") String distritoElectoral,
        @Param("pi_estado_cifra") String estadoCifra,
        @Param("pi_tipo_cifra") String tipoCifra
    );

    @Query(value = "SELECT * FROM fn_cifra_consulta_resumen(:pi_esquema, :pi_tipo_eleccion,:pi_distrito_electoral, :pi_estado_cifra, :pi_tipo_cifra)", nativeQuery = true)
    public Map<String, Object> obtenerConsultaResumen(
        @Param("pi_esquema") String esquema,
        @Param("pi_tipo_eleccion") String tipoEleccion,
        @Param("pi_distrito_electoral") String distritoElectoral,
        @Param("pi_estado_cifra") String estadoCifra,
        @Param("pi_tipo_cifra") String tipoCifra
    );

    @Query(value = "SELECT * FROM fn_cifra_obtiene_porcentaje_avance_mesas(:pi_esquema,:pi_tipo_eleccion,:pi_distrito_electoral)", nativeQuery = true)
    public BigDecimal obtenerPorcentajeAvance(
        @Param("pi_esquema") String esquema,
        @Param("pi_tipo_eleccion") String tipoEleccion,
        @Param("pi_distrito_electoral") String distritoElectoral
    );

    @Query(value = "CALL sp_cifra_consolida_votos_agrupacion(:pi_esquema, :pi_tipo_eleccion, :pi_distrito_electoral, :pi_codigo_usuario, :pi_nombre_pc,:po_resultado,:po_mensaje)", nativeQuery = true)
    public Map<String, Object> cifraConsolidaVotosAgrupacion(
        @Param("pi_esquema") String esquema,
        @Param("pi_tipo_eleccion") String tipoEleccion,
        @Param("pi_distrito_electoral") String distritoElectoral,
        @Param("pi_codigo_usuario") String codigoUsuario,
        @Param("pi_nombre_pc") String nombrePc,
        @Param("po_resultado") Integer poResultado,
		@Param("po_mensaje") String poMensaje
    );

    @Query(value = "CALL sp_cifra_reparte_curules(:pi_esquema, :pi_tipo_eleccion, :pi_distrito_electoral, :pi_codigo_usuario, :pi_nombre_pc, :pi_forzarcalculo,:po_resultado,:po_mensaje)", nativeQuery = true)
    public Map<String, Object> cifraReparteCurules(
        @Param("pi_esquema") String esquema,
        @Param("pi_tipo_eleccion") String tipoEleccion,
        @Param("pi_distrito_electoral") String distritoElectoral,
        @Param("pi_codigo_usuario") String codigoUsuario,
        @Param("pi_nombre_pc") String nombrePc,
        @Param("pi_forzarcalculo") Integer forzarCalculo,
        @Param("po_resultado") Integer poResultado,
		@Param("po_mensaje") String poMensaje
    );

    @Query(value = "SELECT * FROM fn_cifra_listado_votos_empate(:pi_esquema, :pi_tipo_eleccion, :pi_distrito_electoral)", nativeQuery = true)
    public List<Map<String, Object>> obtenerListadoVotosEmpate(
        @Param("pi_esquema") String esquema,
        @Param("pi_tipo_eleccion") String tipoEleccion,
        @Param("pi_distrito_electoral") String distritoElectoral
    );

    @Query(value = "CALL sp_cifra_actualiza_resolucion(:pi_esquema, :pi_tipo_eleccion, :pi_distrito_electoral, :pi_agrupacion_politica, :pi_tipo_caso, :pi_numero_resolucion, :pi_tipo_empate, :pi_descripcion_resolucion, :pi_codigo_usuario, :pi_nombre_pc,:po_resultado,:po_mensaje)", nativeQuery = true)
    public Map<String, Object> actualizarResolucion(
            @Param("pi_esquema") String esquema,
            @Param("pi_tipo_eleccion") String tipoEleccion,
            @Param("pi_distrito_electoral") String distritoElectoral,
            @Param("pi_agrupacion_politica") String agrupacionPolitica,
            @Param("pi_tipo_caso") String tipoCaso,
            @Param("pi_numero_resolucion") String numeroResolucion,
            @Param("pi_tipo_empate") String tipoEmpate,
            @Param("pi_descripcion_resolucion") String descripcionResolucion,
            @Param("pi_codigo_usuario") String codigoUsuario,
            @Param("pi_nombre_pc") String nombrePc,
            @Param("po_resultado") Integer poResultado,
            @Param("po_mensaje") String poMensaje
    );


	
}
