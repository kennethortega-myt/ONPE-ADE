package pe.gob.onpe.sceorcbackend.model.dto.stae.nacion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TransmisionEnvioTramaDTO implements Serializable {

		String proceso;
    List<ActasTransmitidasDTO> actasTransmitidas;

}
