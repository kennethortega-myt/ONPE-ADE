package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.UbigeoDestructurado;

public interface UbigeoDestructuradoRepository extends CrudRepository<UbigeoDestructurado, Long> {


	// Por ejemplo: departamento
	
	@Query(value = "SELECT DISTINCT mu3.n_ubigeo_pk AS n_ubigeo_pk, mu3.c_nombre AS c_nombre " +
            "FROM mae_ubigeo mu1 " +
            "JOIN mae_ubigeo mu2 ON mu1.n_ubigeo_padre = mu2.n_ubigeo_pk " +
            "JOIN mae_ubigeo mu3 ON mu2.n_ubigeo_padre = mu3.n_ubigeo_pk " +
            "JOIN det_ubigeo_eleccion due ON mu1.n_ubigeo_pk = due.n_ubigeo " +
            "WHERE SUBSTRING(mu1.c_ubigeo, 5, 2) <> '00' AND mu1.n_activo = 1 AND due.n_eleccion = :nEleccion"
            + " ORDER BY mu3.c_nombre ",
    nativeQuery = true)
	List<UbigeoDestructurado> getUbigeoNivel3(@Param("nEleccion") Long nEleccion);
	
	// Por ejemplo: provincias
	@Query(value = "SELECT DISTINCT mu2.n_ubigeo_pk AS n_ubigeo_pk, mu2.c_nombre AS c_nombre " +
            "FROM mae_ubigeo mu1 " +
            "JOIN mae_ubigeo mu2 ON mu1.n_ubigeo_padre = mu2.n_ubigeo_pk " +
            "JOIN mae_ubigeo mu3 ON mu2.n_ubigeo_padre = mu3.n_ubigeo_pk " +
            "JOIN det_ubigeo_eleccion due ON mu1.n_ubigeo_pk = due.n_ubigeo " +
            "WHERE SUBSTRING(mu1.c_ubigeo, 5, 2) <> '00' AND mu1.n_activo = 1 AND mu2.n_activo = 1 AND mu2.n_ubigeo_padre = :ubigeoPadre AND due.n_eleccion = :nEleccion",
    nativeQuery = true)
	List<UbigeoDestructurado> getUbigeoNivel2(@Param("ubigeoPadre") Long ubigeoPadre, @Param("nEleccion") Long nEleccion);
	
	// Por ejemplo: distritos
	@Query(value = "SELECT DISTINCT mu1.n_ubigeo_pk AS n_ubigeo_pk, mu1.c_nombre AS c_nombre " +
            "FROM mae_ubigeo mu1 " +
            "JOIN mae_ubigeo mu2 ON mu1.n_ubigeo_padre = mu2.n_ubigeo_pk " +
            "JOIN mae_ubigeo mu3 ON mu2.n_ubigeo_padre = mu3.n_ubigeo_pk " +
            "JOIN det_ubigeo_eleccion due ON mu1.n_ubigeo_pk = due.n_ubigeo " +
            "WHERE SUBSTRING(mu1.c_ubigeo, 5, 2) <> '00' AND mu1.n_activo = 1 AND mu2.n_activo = 1 AND mu1.n_ubigeo_padre = :ubigeoPadre AND due.n_eleccion = :nEleccion",
    nativeQuery = true)
	List<UbigeoDestructurado> getUbigeoNivel1(@Param("ubigeoPadre") Long ubigeoPadre, @Param("nEleccion") Long nEleccion);

}
