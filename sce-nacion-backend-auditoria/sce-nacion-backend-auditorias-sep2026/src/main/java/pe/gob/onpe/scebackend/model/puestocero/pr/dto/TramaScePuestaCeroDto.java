package pe.gob.onpe.scebackend.model.puestocero.pr.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TramaScePuestaCeroDto {
    private boolean puestaCero;
    private String mensaje;
    private boolean limpiarColeccionesPR;
    private boolean actualizarMaeImportar;
    private boolean limpiarArchivosNFS;
    private boolean limpiarMensajesColaMQ;

}
