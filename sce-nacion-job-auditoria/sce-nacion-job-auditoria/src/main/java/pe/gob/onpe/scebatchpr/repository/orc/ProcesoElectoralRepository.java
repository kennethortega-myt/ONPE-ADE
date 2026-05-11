package pe.gob.onpe.scebatchpr.repository.orc;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.scebatchpr.entities.orc.ProcesoElectoral;

public interface ProcesoElectoralRepository extends JpaRepository<ProcesoElectoral, Long> {
    ProcesoElectoral findByActivo(Integer activo);
}