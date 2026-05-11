package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabParametro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CabParametroRepository extends JpaRepository<CabParametro, Integer> , JpaSpecificationExecutor<CabParametro> {

  CabParametro findByParametroAndActivo(String parametro, Integer activo);

}
