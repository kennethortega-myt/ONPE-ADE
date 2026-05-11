package pe.gob.onpe.scebackend.model.dto.transmision;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DetActaPreferencialPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = -7495594931354479747L;
	private Long id;
	private String idOrc;
    private String idDetalleActa;
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
