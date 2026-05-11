package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

@Data
public class AreaDTO {
    private String name;
    private PuntoDTO topLeft;
    private PuntoDTO bottomRight;

    public void ajustarNegativos() {
        if (topLeft != null) topLeft.ajustarNegativos();
        if (bottomRight != null) bottomRight.ajustarNegativos();
    }
}
