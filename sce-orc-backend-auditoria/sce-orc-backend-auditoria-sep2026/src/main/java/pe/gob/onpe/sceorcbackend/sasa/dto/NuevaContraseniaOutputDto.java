package pe.gob.onpe.sceorcbackend.sasa.dto;

import lombok.Data;

@Data
public class NuevaContraseniaOutputDto {
    private Boolean success;
    private String message;
    private String titulo;
    private Void data;
}
