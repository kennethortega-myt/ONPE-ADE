package pe.gob.onpe.scebackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class ActaInfoDTO {
    private String codigoMesa;
    private String numeroCopia;
    private String digitoChequeoEscrutinio;

    public ActaInfoDTO(String codigoMesa, String numeroCopia, String digitoChequeoEscrutinio) {
        this.codigoMesa = codigoMesa;
        this.numeroCopia = numeroCopia;
        this.digitoChequeoEscrutinio = digitoChequeoEscrutinio;
    }


}
