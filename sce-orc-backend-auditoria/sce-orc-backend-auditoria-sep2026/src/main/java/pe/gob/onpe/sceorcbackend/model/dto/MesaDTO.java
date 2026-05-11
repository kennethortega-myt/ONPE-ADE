package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MesaDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1154271834434210894L;
    private Long id;
    private String mesa;
}
