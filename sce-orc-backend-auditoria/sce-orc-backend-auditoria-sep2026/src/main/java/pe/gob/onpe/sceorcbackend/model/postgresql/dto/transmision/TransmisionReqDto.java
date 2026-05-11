package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision;



import lombok.*;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransmisionReqDto implements Serializable {

    private static final long serialVersionUID = -213040931081436217L;

    private Long idTransmision;
    private String centroComputo;
    private String acronimoProceso;
    private ActaPorTransmitirReqDto actaTransmitida;
    private Integer estadoTransmitidoNacion;
    private String accion;
    private String subAccion;
    private String fechaTransmision;
    private String fechaRegistro;
    private String usuarioTransmision;


}
