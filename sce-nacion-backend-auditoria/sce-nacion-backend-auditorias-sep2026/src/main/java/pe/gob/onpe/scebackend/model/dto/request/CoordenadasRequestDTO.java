package pe.gob.onpe.scebackend.model.dto.request;

import lombok.Data;
import pe.gob.onpe.scebackend.model.dto.AreaDTO;
import java.util.List;

@Data
public class CoordenadasRequestDTO {
    private List<AreaDTO> areas;
    private String image;
    private String abreviatura;
}
