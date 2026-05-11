package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class FiltroMesaDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8342365838474312599L;
    private Long idLocalVotacion;
}
