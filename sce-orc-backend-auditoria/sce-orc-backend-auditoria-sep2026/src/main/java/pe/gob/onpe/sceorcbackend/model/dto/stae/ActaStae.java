package pe.gob.onpe.sceorcbackend.model.dto.stae;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ActaStae implements Serializable {

	private static final long serialVersionUID = 1916587895770104091L;
	private Long cvas;
    private Integer eleccion;
    private String estadoActa;
    private String estadoActaResolucion;
    private String estadoCompu;
    private String estadoErrorMaterial;
    private String horaEscrutinio;
    private String horaInstalacion;
    private String numeroActa;
    private String numeroCopia;
    private String observaciones;
    private Long totalVotos;
    private Long votosCalculados;
    private List<DetActaStae> detalleActa;
}
