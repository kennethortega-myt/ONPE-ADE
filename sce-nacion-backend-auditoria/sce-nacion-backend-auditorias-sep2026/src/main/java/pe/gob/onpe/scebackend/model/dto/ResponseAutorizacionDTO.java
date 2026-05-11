package pe.gob.onpe.scebackend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseAutorizacionDTO {

    private String message;
    private boolean isContinue;
    private Long idAutorizacion;
    private boolean autorizado;

}
