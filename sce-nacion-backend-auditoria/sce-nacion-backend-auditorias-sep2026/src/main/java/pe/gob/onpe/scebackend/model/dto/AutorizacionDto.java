package pe.gob.onpe.scebackend.model.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AutorizacionDto implements Serializable {
    private static final long serialVersionUID = 1L;

    Long id;
    Long numeroAutorizacion;
    String estadoAprobacion;
    String descEstadoAprobacion;
    Integer autorizacion;
    String tipoAutorizacion;
    String descTipoAutorizacion;
    String detalle;
    String codigoCentroComputo;
    String nombreCentroComputo;
    String tipoDocumento;
    String descTipoDocumento;
    Long idDocumento;
    String usuarioCreacion;
    String fechaCreacion;
    String usuarioModificacion;
    String fechaModificacion;
}
