package pe.gob.onpe.scebackend.model.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentoElectoralRequestDto {

    private Integer id;

    private String nombre;

    private String abreviatura;

    private Integer tipoImagen;

    private Integer escanerAmbasCaras;

    private Integer tamanioHoja;

    private Integer multipagina;

    private Integer codigoBarraOrientacion;

    private Integer activo;

    private String usuario;

    private String rangoInicial;

    private String rangoFinal;

    private String digitoChequeo;

    private String digitoError;

    private Integer configuracionGeneral;

    private Integer visible;
}
