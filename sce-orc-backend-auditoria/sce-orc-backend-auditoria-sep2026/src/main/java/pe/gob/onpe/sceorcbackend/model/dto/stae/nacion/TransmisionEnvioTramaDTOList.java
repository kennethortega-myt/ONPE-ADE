package pe.gob.onpe.sceorcbackend.model.dto.stae.nacion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TransmisionEnvioTramaDTOList implements Serializable {

    Boolean success;
    String message;
    List<TransmisionEnvioTramaDTO> data;
    
}
