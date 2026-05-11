package pe.gob.onpe.sceorcbackend.model.dto.request.resoluciones;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.utils.DateTimeUtil;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class ResolucionDevueltasRequest implements Serializable {
 
	@Serial
	private static final long serialVersionUID = -1401517382635294277L;
	private Long id;
    private Long idArchivo;
    private String nombreArchivo;
    private Integer procedencia;
    private  Date fechaResolucion;
    private Date fechaResolucion2;
    @JsonFormat(pattern = SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH, timezone = DateTimeUtil.AMERICA_LIMA)
    private Date fechaRegistro;
    private String numeroExpediente;
    private String numeroResolucion;
    private Integer tipoResolucion;
    private String tipoPasarNulos;
    private String descripcionTipoResolucion;
    private String estadoResolucion;
    private String descripcionEstadoResolucion;
    private Integer numeroPaginas;
    private String estadoDigitalizacion;
    private String descripcionEstadoDigitalizacion;
    private String mesa;
    private String numeroActa;
    private String codigoEleccion;
    private String codigoProceso;
}
