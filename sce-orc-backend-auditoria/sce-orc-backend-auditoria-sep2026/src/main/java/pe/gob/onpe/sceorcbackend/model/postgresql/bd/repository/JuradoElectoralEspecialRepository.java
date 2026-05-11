package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.JuradoElectoralEspecial;

public interface JuradoElectoralEspecialRepository extends JpaRepository<JuradoElectoralEspecial, Integer> ,
            JpaSpecificationExecutor<JuradoElectoralEspecial>{
	
    Optional<JuradoElectoralEspecial> findByCodigoCentroComputo(String codigoCentroComputo);

}
