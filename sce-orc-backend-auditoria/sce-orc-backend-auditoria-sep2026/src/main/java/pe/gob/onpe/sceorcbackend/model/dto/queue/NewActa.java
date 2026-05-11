package pe.gob.onpe.sceorcbackend.model.dto.queue;

import lombok.Builder;
import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;

@Data
@Builder
public class NewActa {
    private Long actaId;
    private Long fileId;
    private Long type;
    private String usuario;
    private String codigocc;
    private String abrevProceso;

    /**
     * Crea un NewActa a partir de datos comunes.
     */
    public static NewActa from(Long actaId, Long fileId, Long tipoArchivo, TokenInfo tokenInfo) {
        return NewActa.builder()
                .actaId(actaId)
                .fileId(fileId)
                .type(tipoArchivo)
                .usuario(tokenInfo.getNombreUsuario())
                .codigocc(tokenInfo.getCodigoCentroComputo())
                .abrevProceso(tokenInfo.getAbrevProceso())
                .build();
    }
}
