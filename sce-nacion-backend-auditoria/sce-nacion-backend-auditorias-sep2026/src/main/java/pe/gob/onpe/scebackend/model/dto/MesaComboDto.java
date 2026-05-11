package pe.gob.onpe.scebackend.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MesaComboDto {

    private Long id;

    private String mesa;

}
