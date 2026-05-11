package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.Data;

@Data
public class DigitizationListActasItem {
    private Long actaId;
    private String mesa;
    private String estado;
    private String acta1FileId;
    private String acta1Status;
    private String nroActaEscrutinio;
    private String acta2FileId;
    private String acta2Status;
    private String nroActaInstalacionSufragio;
}
