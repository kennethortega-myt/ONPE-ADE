package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.PadronElectoral;

import java.util.List;
import java.util.Optional;

public interface PadronElectoralRepository extends JpaRepository<PadronElectoral, Long>, JpaSpecificationExecutor<PadronElectoral> {
	
    Optional<PadronElectoral> findByDocumentoIdentidad(String dni);

    Optional<PadronElectoral> findByDocumentoIdentidadAndCodigoMesa(String dni, String mesa);
    
    List<PadronElectoral> findPadronElectoralByCodigoMesaOrderByOrden(String codigoMesa);

    boolean existsByActivo(Integer activo);

    @Modifying
    @Query("DELETE FROM PadronElectoral")
    void deleteAllInBatch();

    Optional<PadronElectoral> findByDocumentoIdentidadAndMesaId(String dni, Integer mesaId);
}
