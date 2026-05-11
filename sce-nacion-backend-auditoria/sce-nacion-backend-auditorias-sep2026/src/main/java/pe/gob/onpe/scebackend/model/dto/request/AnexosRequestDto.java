package pe.gob.onpe.scebackend.model.dto.request;

import lombok.Data;

@Data
public class AnexosRequestDto {

    private String esquema;
    private Integer centroComputo;
    private String usuarioConsulta;
}
