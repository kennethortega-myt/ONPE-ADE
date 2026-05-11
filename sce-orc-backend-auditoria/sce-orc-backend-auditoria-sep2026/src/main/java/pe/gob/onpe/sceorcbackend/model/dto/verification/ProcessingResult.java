package pe.gob.onpe.sceorcbackend.model.dto.verification;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OmisoVotante;
import java.util.List;

@Data
public class ProcessingResult {
    private boolean observaciones;
    private String mensajeObservacion;
    private List<OmisoVotante> tabOmisoVotanteList;

    public ProcessingResult(boolean observaciones, String mensaje, List<OmisoVotante> tabOmisoVotanteList) {
        this.observaciones = observaciones;
        this.mensajeObservacion = mensaje;
        this.tabOmisoVotanteList = tabOmisoVotanteList;
    }
}