package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pe.gob.onpe.scebackend.model.orc.entities.CabParametro;

public interface CabParametroRepository extends JpaRepository<CabParametro, Long> , JpaSpecificationExecutor<CabParametro> {

	List<CabParametro> findAll();

    CabParametro findByParametroAndActivo(String parametro, Integer activo);
	
}
