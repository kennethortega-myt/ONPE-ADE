package pe.gob.onpe.scebackend.model.dto.response;

import lombok.Data;

@Data
public class DocumentoElectoralResponseDto {

    private Integer id;

    private String nombre;

    private String abreviatura;

    private Integer tipoImagen;

    private Integer escanerAmbasCaras;

    private Integer tamanioHoja;

    private Integer multipagina;

    private Integer codigoBarraOrientacion;
    private Integer activo;

    private Integer configuracionGeneral;

}
