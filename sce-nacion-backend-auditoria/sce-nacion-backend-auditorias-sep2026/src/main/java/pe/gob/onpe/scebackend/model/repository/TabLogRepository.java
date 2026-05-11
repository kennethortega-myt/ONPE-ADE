package pe.gob.onpe.scebackend.model.repository;

import pe.gob.onpe.scebackend.model.entities.TabLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TabLogRepository  extends JpaRepository<TabLog, Long>, JpaSpecificationExecutor<TabLog> {

}
