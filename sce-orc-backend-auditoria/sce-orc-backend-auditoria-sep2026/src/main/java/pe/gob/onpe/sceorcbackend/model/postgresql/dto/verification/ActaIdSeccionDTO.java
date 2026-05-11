package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

import lombok.Data;

@Data
public class ActaIdSeccionDTO {

    private Long actaId;
    private Integer eleccionId;
    private Long seccionCount;

    public ActaIdSeccionDTO(Long actaId, Integer eleccionId, Long seccionCount) {
        this.actaId = actaId;
        this.eleccionId = eleccionId;
        this.seccionCount = seccionCount;
    }


    @Override
    public String toString() {
        return "ActaIdSeccionDTO{" +
                "actaId=" + actaId +
                ", eleccionId=" + eleccionId +
                ", seccionCount=" + seccionCount +
                '}';
    }
}
