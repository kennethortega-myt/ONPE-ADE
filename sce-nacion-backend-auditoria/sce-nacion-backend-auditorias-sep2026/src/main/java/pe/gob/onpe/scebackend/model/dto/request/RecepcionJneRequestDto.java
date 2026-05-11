package pe.gob.onpe.scebackend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecepcionJneRequestDto {
    private String hash;
    private String token;
    private String dniUsuarioCarga;
    private CargaJne carga;
}
