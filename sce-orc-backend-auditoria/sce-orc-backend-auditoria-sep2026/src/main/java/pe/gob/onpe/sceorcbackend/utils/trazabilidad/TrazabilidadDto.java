package pe.gob.onpe.sceorcbackend.utils.trazabilidad;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TrazabilidadDto implements Serializable {
	private static final long serialVersionUID = 871055457974325919L;
	private InfoActa infoActa;
    private List<ItemHistory> history;
}
