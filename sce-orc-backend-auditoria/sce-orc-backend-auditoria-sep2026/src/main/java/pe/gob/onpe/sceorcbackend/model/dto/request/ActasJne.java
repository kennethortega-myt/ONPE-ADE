package pe.gob.onpe.sceorcbackend.model.dto.request;

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
