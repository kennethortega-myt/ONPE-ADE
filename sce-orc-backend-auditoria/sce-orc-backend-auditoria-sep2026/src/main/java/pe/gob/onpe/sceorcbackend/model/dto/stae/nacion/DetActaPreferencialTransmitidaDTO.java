package pe.gob.onpe.sceorcbackend.model.dto.stae.nacion;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DetActaPreferencialTransmitidaDTO implements Serializable {
	
	@Serial
  private static final long serialVersionUID = 3299591023534890522L;
	
	private Long id;
    private Long idDetalleActa;
    private Long idActa;
    private Long idDistritoElectoral;
    private Integer lista;
    private Long nvotos;
    private Long nposicion;
    private String cilegiblev2;
    private Long nvotosv1;
    private Long nvotosv2;
    private String cilegiblev1;
    private Integer nactivo;
    private String cilegible;
    private String cestadoErrorMaterial;
    private Long nvotosAutomatico;

}
