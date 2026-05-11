package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

@Data
public class PuntoDTO {

    private double x;
    private double y;

    public void ajustarNegativos() {
        if (this.x < 0) this.x = 0;
        if (this.y < 0) this.y = 0;
    }

}
