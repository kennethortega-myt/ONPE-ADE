package pe.gob.onpe.sceorcbackend.model.postgresql.dto.digitalizacion;

import lombok.Builder;
import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DocumentoElectoral;

@Data
@Builder
public class DocumentoElectoralInfo {
    DocumentoElectoral documentoElectoralAe;
    DocumentoElectoral documentoElectoralAeh;
    DocumentoElectoral documentoElectoralAis;
    boolean valid;
    String errorMessage;

    public DocumentoElectoralInfo(DocumentoElectoral documentoElectoralAe,
                                  DocumentoElectoral documentoElectoralAeh,
                                  DocumentoElectoral documentoElectoralAis, boolean valid, String errorMessage) {
        this.documentoElectoralAe = documentoElectoralAe;
        this.documentoElectoralAeh = documentoElectoralAeh;
        this.documentoElectoralAis = documentoElectoralAis;
        this.valid = valid;
        this.errorMessage = errorMessage;
    }
}
