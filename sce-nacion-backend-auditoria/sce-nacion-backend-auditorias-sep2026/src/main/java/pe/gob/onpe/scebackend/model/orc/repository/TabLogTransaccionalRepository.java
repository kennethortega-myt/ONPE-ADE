package pe.gob.onpe.scebackend.model.orc.repository;

import pe.gob.onpe.scebackend.model.orc.entities.TabLogTransaccional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TabLogTransaccionalRepository extends JpaRepository<TabLogTransaccional, Long>,
    JpaSpecificationExecutor<TabLogTransaccional> {

}

