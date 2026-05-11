package pe.gob.onpe.scebackend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsultaCifraRepartidoraRequestDto {    
    private String codEleccion;
    private String codDistritoElectoral;
    private String estadoCifra;
    private String tipoCifra;
    
    private Long idProceso;
    private String usuario;

}
