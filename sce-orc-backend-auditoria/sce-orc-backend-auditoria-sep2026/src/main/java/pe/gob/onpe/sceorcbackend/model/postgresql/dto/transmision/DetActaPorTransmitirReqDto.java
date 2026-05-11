package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision;



import lombok.*;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetActaPorTransmitirReqDto implements Serializable {

    private static final long serialVersionUID = -9114170912482683290L;

    private Long idActaDetalle;
    private Long idActa;
    private Long idAgrupacionPolitica;
    private Long posicion;
    private Long votos;
    private Long votosAutomatico;
    private Long votosManual1;
    private Long votosManual2;
    private String estadoErrorMaterial;
    private String ilegible;
    private String ilegiblev2;
	private String ilegiblev1;
    private Integer activo;
    private List<DetActaPreferencialPorTransmitirReqDto> detActaPreferencial;
    private List<DetActaOpcionPorTransmitirReqDto> detActaOpcion;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;
    private Integer estado;


}
