package pe.gob.onpe.scebackend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActasJne {
    private String numeroMesa;
    private Integer idTipoEleccion;
    private String numeroCopia;
    private String digitoVerificador;
}
