package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.scebackend.model.orc.entities.Usuario;
import pe.gob.onpe.scebackend.model.orc.projections.reporte.AsistenciaMmEscrutinioProjection;

public interface AsistenciaMmEscrutinioRepository extends JpaRepository<Usuario, Long> {

	@Query(
			value = """
                SELECT
                    c_codigo_ambito_electoral AS codDescODPE,
                    c_codigo_centro_computo AS codDescCC,
                    c_mesa AS numMesaMadre,
                    c_departamento AS descDepartamento,
                    c_distrito AS descDistrito,
                    c_provincia AS descProvincia,
                    c_codigo_ubigeo AS codUbigeo,
                    n_total_electores_habiles AS eleHabil,
                    c_documento_identidad AS numEle,
                    c_apellido_nombre AS votante,
                    c_cargo AS descargo,
                    n_nro AS numero,
                    c_sexo AS sexo
                FROM fn_reporte_lista_asistencia_mm_escrutinio(
                    :pi_esquema,
                    :pi_eleccion,
                    :pi_n_centro_computo,
                    :pi_c_ubigeo,
                    :pi_n_mesa
                )
                """,
			nativeQuery = true
	)
	List<AsistenciaMmEscrutinioProjection> listaAsistenciaMmEscrutinio(
			@Param("pi_esquema") String piEsquema,
			@Param("pi_eleccion") Integer piEleccion,
			@Param("pi_n_centro_computo") Integer piNCentroComputo,
			@Param("pi_c_ubigeo") String piCUbigeo,
			@Param("pi_n_mesa") Integer piNMesa
	);
}