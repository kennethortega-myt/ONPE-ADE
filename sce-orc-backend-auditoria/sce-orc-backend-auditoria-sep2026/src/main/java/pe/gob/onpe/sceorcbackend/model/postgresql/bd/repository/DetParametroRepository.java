package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetParametro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DetParametroRepository extends JpaRepository<DetParametro, Integer> {

  List<DetParametro> findByParametroId(Long idParametro);

    List<DetParametro> findByActivoAndParametroId(Integer activo,Long idParametro);

  Optional<DetParametro> findById(Long id);

  List<DetParametro> findByNombre(String nombre);
  
  @Query(value = "SELECT det.valor " +
          "FROM DetParametro det " +
          "JOIN det.parametro cab " +
          "WHERE cab.parametro = :cab and det.nombre = :det ")
  String getHora(@Param("cab") String cab, @Param("det") String det);

    @Modifying
    @Transactional
    @Query("UPDATE DetParametro d " +
            "SET d.activo = :activo, d.fechaModificacion =:fechaModificacion, "
            + "d.usuarioModificacion =:usuario  WHERE d.id =:idParametro ")
    int actualizarEstado(@Param("activo") Integer activo,
                           @Param("fechaModificacion") Date fechaModificacion,
                           @Param("usuario") String usuario,
                           @Param("idParametro") Long idParametro);

    @Modifying
    @Transactional
    @Query("UPDATE DetParametro d " +
            "SET d.valor = :valor, d.fechaModificacion = :fechaModificacion, " +
            "d.usuarioModificacion = :usuario " +
            "WHERE d.nombre = :cNombre " +
            "AND d.parametro.id IN (SELECT p.id FROM CabParametro p WHERE p.parametro = :cParametro)")
    int actualizarValorPorParametroYNombre(@Param("valor") String valor,
                                            @Param("fechaModificacion") Date fechaModificacion,
                                            @Param("usuario") String usuario,
                                            @Param("cParametro") String cParametro,
                                            @Param("cNombre") String cNombre);
  
}
