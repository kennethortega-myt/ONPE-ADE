package pe.gob.onpe.sceorcbackend.model.dto.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationActaDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionActaRequest {
    private Long actaId;
    private String nombreUsuario;
    private String codigoCentroComputo;
    private String abrevProceso;
    private VerificationActaDTO body;
}
