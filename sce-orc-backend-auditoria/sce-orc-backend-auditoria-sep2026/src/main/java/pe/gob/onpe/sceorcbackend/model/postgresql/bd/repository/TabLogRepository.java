package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;

public interface TabLogRepository  extends JpaRepository<TabLog, Long>, JpaSpecificationExecutor<TabLog> {



  @Modifying
  @Query(value = "DELETE FROM tab_log_transaccion WHERE d_fecha_registro < :fechaLimite", nativeQuery = true)
  void deleteByFechaRegistroBefore(@Param("fechaLimite") Date fechaLimite);


  @Modifying
  @Query(value = "DELETE FROM tab_log_transaccion", nativeQuery = true)
  void deleteByFechaRegistroBefore();
}
