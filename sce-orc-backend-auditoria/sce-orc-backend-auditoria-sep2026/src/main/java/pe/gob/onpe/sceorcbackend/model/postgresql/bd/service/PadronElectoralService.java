package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;


import org.springframework.data.domain.Page;
import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.PadronElectoralBusquedaDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.PadronElectoralResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.PadronElectoral;
import java.util.List;
import java.util.Optional;

public interface PadronElectoralService extends CrudService<PadronElectoral> {

    Optional<PadronElectoral> findById(Long id);

    Optional<PadronElectoral> findByDocumentoIdentidad(String dni);

    Optional<PadronElectoral> findByDocumentoIdentidadAndMesa(String dni, String mesa);
    
    List<PadronElectoral> findPadronElectoralByCodigoMesaOrderByOrden(String codigoMesa);

    boolean existsByActivo(Integer activo);

    Optional<PadronElectoral> findByDocumentoIdentidadAndMesaId(String dni, Integer mesaId);
    Page<PadronElectoralResponse> buscarElectores(PadronElectoralBusquedaDto criterios, int page, int size);

}
