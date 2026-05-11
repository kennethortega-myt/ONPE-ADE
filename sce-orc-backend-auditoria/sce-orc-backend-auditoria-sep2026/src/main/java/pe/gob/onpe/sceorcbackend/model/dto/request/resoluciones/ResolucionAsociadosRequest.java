package pe.gob.onpe.sceorcbackend.model.dto.request.resoluciones;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ActaBean;
import pe.gob.onpe.sceorcbackend.utils.DateTimeUtil;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ResolucionAsociadosRequest implements Serializable {
 
	@Serial
  private static final long serialVersionUID = -1401517382635294277L;
	private Long id;
    private Long idArchivo;
    private String nombreArchivo;
    private Integer procedencia;
    private  Date fechaResolucion;//para registro
    private Date fechaResolucion2;//para mostrar, edicion
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
    private String usuarioAsociado;
    private List<ActaBean> actasAsociadas = new ArrayList<>();

}
