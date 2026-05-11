package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.VersionCC;

public interface VersionCCRepository extends JpaRepository<VersionCC, Long> {
}
