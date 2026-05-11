package pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class AgrupolBean implements Serializable {

	  private static final long serialVersionUID = -4436584115214520701L;
	  private Long idAgrupol;
    private String codiAgrupol;
    private String idDetActa;
    private String nombreAgrupacionPolitica;
    private String votos;
    private Long posicionActa;
    private Long posicion;
    private String errorMaterial;
    private String ilegible;
    private String activo;
    private Integer estado;
    private List<VotoPreferencialBean> votosPreferenciales;
    private List<VotoOpcionBean> votosOpciones;

}
