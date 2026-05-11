package pe.gob.onpe.sceorcbackend.model.dto.verification;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class BarCodeInfo {
    String codigoBarra;
    String nroMesa;
    String nroCopia;
    String nroCopiaAndDigito;
    String digitoChequeo;


}
