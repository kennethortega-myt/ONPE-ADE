package pe.gob.onpe.sceorcbackend.model.dto.response.actas;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class ActaPorCorregir implements Serializable {
	private static final long serialVersionUID = -5907026987659631984L;
	private ActaPorCorregirListItem acta;
    private List<AgrupolPorCorregir> agrupacionesPoliticas;
    private ItemPorCorregir cvas;
    private List<ItemPorCorregir> observaciones;
}
