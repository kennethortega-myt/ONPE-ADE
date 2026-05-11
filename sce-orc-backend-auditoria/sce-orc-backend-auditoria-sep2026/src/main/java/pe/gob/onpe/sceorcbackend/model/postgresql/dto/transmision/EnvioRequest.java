package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaTransmisionNacion;

@Setter
@Getter
public class EnvioRequest {

	Long idActa;
	String proceso;
	List<ActaTransmisionNacion> transmisiones;
	
}
