package pe.gob.onpe.sceorcbackend.sasa.dto;

import lombok.Data;

@Data
public class RestablecerContraseniaOutputDto {
    private Integer data;
    private String message;
    private Boolean success;
    private String titulo;
}
