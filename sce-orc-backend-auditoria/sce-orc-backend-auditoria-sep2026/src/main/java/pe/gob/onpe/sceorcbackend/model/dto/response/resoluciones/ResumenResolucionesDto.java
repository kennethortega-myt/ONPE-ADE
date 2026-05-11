package pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.dto.request.resoluciones.ResolucionAsociadosRequest;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class ResumenResolucionesDto implements Serializable {

	@Serial
    private static final long serialVersionUID = -5736956415317377586L;
	private int numResolucionesAplicadas;
    private int numResolucionesAnuladas;
    private int numResolucionesSinAplicar;
    private int numResolucionesSinAplicarAsociadas;
    private int numTotalResoluciones;
    private List<ResolucionAsociadosRequest> resoluciones;
}
