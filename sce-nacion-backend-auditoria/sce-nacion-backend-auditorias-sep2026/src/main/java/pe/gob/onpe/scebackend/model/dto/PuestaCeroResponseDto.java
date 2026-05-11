package pe.gob.onpe.scebackend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PuestaCeroResponseDto {
    private boolean success;
    private Long idPuestaCeroStae;
    private Long idPuestaCeroVd;
    private boolean puestaCeroNacion;
    private boolean puestaCeroPr;
    private boolean puestaCeroStae;
    private boolean puestaCeroVd;
    private String message;
}
