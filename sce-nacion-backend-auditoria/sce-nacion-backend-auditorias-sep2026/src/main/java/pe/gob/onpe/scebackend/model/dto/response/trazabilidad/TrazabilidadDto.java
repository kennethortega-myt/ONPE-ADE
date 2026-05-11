package pe.gob.onpe.scebackend.model.dto.response.trazabilidad;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class TrazabilidadDto implements Serializable {
	@Serial
    private static final long serialVersionUID = 871055457974325919L;
	private InfoActa infoActa;
    private List<ItemHistory> history;
}
