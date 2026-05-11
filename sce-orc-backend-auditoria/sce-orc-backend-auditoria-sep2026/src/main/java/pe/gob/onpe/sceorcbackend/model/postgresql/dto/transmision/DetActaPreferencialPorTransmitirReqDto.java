package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision;


import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetActaPreferencialPorTransmitirReqDto implements Serializable {

	private static final long serialVersionUID = -8578501292986782007L;
	private Long id;
	private String idOrc;
    private Long idDetalleActa;
    private Integer idDistritoElectoral;
    private Integer posicion;
    private Integer lista;
    private Long votos;
    private Long votosAutomatico;
    private Long votosv1;
    private Long votosv2;
    private String estadoErrorMaterial;
    private String ilegible;
    private String ilegiblev1;
    private String ilegiblev2;
    private Integer activo;
    private String audUsuarioCreacion;
	private String audFechaCreacion;
	private String audUsuarioModificacion;
	private String audFechaModificacion;

}
