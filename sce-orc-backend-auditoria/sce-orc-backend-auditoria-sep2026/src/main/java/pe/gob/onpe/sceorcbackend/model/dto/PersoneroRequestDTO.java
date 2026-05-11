package pe.gob.onpe.sceorcbackend.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersoneroRequestDTO {
    private List<PersoneroDTO> personeros;
    private Integer tipoFiltro;
    private MesaDTO mesa;
    private Long actaId;
    private String acronimoProceso;
}
