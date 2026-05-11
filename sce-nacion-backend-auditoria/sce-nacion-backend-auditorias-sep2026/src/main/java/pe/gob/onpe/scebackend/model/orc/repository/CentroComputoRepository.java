package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

public interface CentroComputoRepository extends JpaRepository<CentroComputo, Long>, MigracionRepository<CentroComputo, String> {

  @Query(value = "WITH RECURSIVE centros_computo_hijos AS ( "
  		+ "                SELECT cc.* "
  		+ "                FROM mae_centro_computo cc "
  		+ "                WHERE cc.c_codigo = ?1"
  		+ "                UNION ALL "
  		+ "                SELECT padre.* "
  		+ "                FROM mae_centro_computo padre "
  		+ "                INNER JOIN centros_computo_hijos hijo "
  		+ "                ON hijo.n_centro_computo_padre = padre.n_centro_computo_pk "
  		+ "            ) "
  		+ "            SELECT distinct * "
  		+ "            FROM centros_computo_hijos "
  		+ "            ORDER BY n_centro_computo_padre NULLS first", nativeQuery = true)
  public List<CentroComputo> findByCc(String codigo);

  @Query("SELECT distinct c FROM CentroComputo c WHERE c.codigo = ?1")
  public Optional<CentroComputo> findOneByCc(String codigo);

  CentroComputo findByCentroComputoPadreIsNull();
  
  List<CentroComputo> findByCentroComputoPadreIsNotNullOrderByCodigoAsc();
  
  @Query(value = "select distinct mcc.* from tab_mesa tm "
		  + " inner join mae_local_votacion mlv on mlv.n_local_votacion_pk = tm.n_local_votacion  "
		  + " inner join mae_ubigeo mu on mu.n_ubigeo_pk = mlv.n_ubigeo "
		  + " inner join mae_centro_computo mcc on mcc.n_centro_computo_pk = mu.n_centro_computo  "
		  + " where tm.c_mesa = ?1", nativeQuery = true)
  public Optional<CentroComputo> findByCodigoMesa(String codigo);
  
  public Optional<CentroComputo> findByCodigo(String codigo);

}
