package pe.gob.onpe.scebackend.model.orc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.onpe.scebackend.model.orc.entities.TabAutorizacion;

import java.util.List;
import java.util.Optional;

public interface TabAutorizacionRepository extends JpaRepository<TabAutorizacion, Long> {
    Optional<TabAutorizacion> findFirstByDetalleAndEstadoAprobacion(String detalle, String estadoAprobacion);
    Optional<TabAutorizacion> findFirstByEstadoAprobacionInAndUsuarioCreacionAndTipoAutorizacionAndActivo(List<String> estadoAprobacion, String usuarioCreacion, String tipoAutorizacion, Integer activo);
    Optional<TabAutorizacion> findFirstByEstadoAprobacionInAndUsuarioCreacionAndTipoAutorizacionAndTipoDocumentoAndIdDocumentoAndCodigoCentroComputoAndActivo(
            List<String> estadoAprobacion, String usuarioCreacion, String tipoAutorizacion, String tipoDocumento, Long idDocumento, String codigoCentroComputo, Integer activo);
    Optional<TabAutorizacion> findByEstadoAprobacionInAndUsuarioCreacion(List<String> estadoAprobacion, String usuarioCreacion);
    List<TabAutorizacion> findAllByOrderByFechaModificacionDesc();
    List<TabAutorizacion> findAllByActivoOrderByFechaModificacionDesc(Integer activo);
}
