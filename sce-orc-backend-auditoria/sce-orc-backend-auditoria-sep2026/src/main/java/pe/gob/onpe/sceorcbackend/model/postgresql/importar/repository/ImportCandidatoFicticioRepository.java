package pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportCandidatoFicticio;

public interface ImportCandidatoFicticioRepository extends JpaRepository<ImportCandidatoFicticio, Integer> {

}
