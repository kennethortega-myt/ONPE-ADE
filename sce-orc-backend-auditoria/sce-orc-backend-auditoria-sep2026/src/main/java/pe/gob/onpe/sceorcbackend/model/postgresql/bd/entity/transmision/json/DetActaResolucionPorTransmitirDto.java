package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json;



import lombok.*;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetActaResolucionPorTransmitirDto implements Serializable {

    private static final long serialVersionUID = 2862564640766166564L;

    private String id;
    private Long idActa;
    private String estadoActa;
    private Integer correlativo;
    private Integer activo;
    private String audUsuarioCreacion;
	private String audFechaCreacion;
	private String audUsuarioModificacion;
	private String audFechaModificacion;
    private ResolucionPorTransmitirDto resolucionDto;

}
