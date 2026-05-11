package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

@Data
public class NuevaContraseniaOutputDto {
    private Boolean success;
    private String message;
    private String titulo;
    private Void data;
}
