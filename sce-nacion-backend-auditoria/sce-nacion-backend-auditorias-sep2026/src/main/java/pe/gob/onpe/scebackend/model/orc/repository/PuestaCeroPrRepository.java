package pe.gob.onpe.scebackend.model.orc.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.gob.onpe.scebackend.model.orc.entities.PuestaCeroPr;


@Repository
public interface PuestaCeroPrRepository extends JpaRepository<PuestaCeroPr, Long> {
   
	
	
}