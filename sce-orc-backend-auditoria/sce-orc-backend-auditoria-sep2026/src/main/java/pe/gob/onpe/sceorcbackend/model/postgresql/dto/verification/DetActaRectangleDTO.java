package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.json.DetActaRectangleVoteData;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class DetActaRectangleDTO implements Serializable {

    private Long id;
    private Integer seccion;
    private Long actaId;
    private Integer eleccionId;
    private Long archivo;
    private String type;
    private Boolean valid;
    private String totalVotos;
    private DetActaRectangleVoteData values;
    private String abreviatura;


    public DetActaRectangleDTO(Long id, Integer seccion, Long actaId, Integer eleccionId, Long archivo,
                               String type, Boolean valid, String totalVotos, Object  values, String abreviatura) {
        this.id = id;
        this.seccion = seccion;
        this.actaId = actaId;
        this.eleccionId = eleccionId;
        this.archivo = archivo;
        this.type = type;
        this.valid = valid;
        this.totalVotos = totalVotos;
        this.values = (DetActaRectangleVoteData) values;
        this.abreviatura = abreviatura;
    }
}
