package pe.gob.onpe.sceorcbackend.utils.trazabilidad;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TrazabilidadMesaDto implements Serializable {
	private static final long serialVersionUID = -5259962819148024203L;
	private String mesa;
    private String estadoMesa;
    private String descripcionEstadoMesa;
    private List<TrazabilidadDto> trazabilidadActas;
}
