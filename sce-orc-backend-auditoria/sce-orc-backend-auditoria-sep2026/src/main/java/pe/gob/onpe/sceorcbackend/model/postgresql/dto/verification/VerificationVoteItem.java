package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;
import java.util.Objects;

@Data
public class VerificationVoteItem {
    private Integer position;
    private String positionToken;
    private Long fileId;
    private String systemValue;
    private String userValue;
    private String nombreAgrupacionPolitica;
    private Integer estado;
    private List<VerificationVoteRevocatoriaItem> votoRevocatoria;
    private List<VerificationVotePreferencialItem> votoPreferencial;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String filePngUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isEditable;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationVoteItem that = (VerificationVoteItem) o;
        return Objects.equals(position, that.position) && Objects.equals(fileId, that.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, fileId);
    }
}
