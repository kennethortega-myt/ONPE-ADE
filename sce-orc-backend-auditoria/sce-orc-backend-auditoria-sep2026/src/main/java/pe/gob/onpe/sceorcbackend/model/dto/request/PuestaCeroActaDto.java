package pe.gob.onpe.sceorcbackend.model.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PuestaCeroActaDto {

    @Valid
    private List<Mesa> mesas;

    @Data
    public static class Mesa {

        @Size(min = 6, max = 6)
        String codigo;

        @Size(min = 1, max = 5)
        List<Integer> elecciones;

    }

}
