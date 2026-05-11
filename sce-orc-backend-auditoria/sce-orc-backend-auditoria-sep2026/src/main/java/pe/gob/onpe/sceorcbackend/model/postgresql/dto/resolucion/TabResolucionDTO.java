package pe.gob.onpe.sceorcbackend.model.postgresql.dto.resolucion;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;

import java.io.Serializable;
import java.util.Date;

@Data
public class TabResolucionDTO implements Serializable {
	private static final long serialVersionUID = 2339244689346410195L;
	private Long id;
    private Archivo archivoResolucion;
    private Archivo archivoResolucionPdf;
    private Integer	procedencia;
    private Date fechaResolucion;
    private String numeroExpediente;
    private String numeroResolucion;
    private Integer tipoResolucion;
    private String estadoResolucion;
    private String estadoDigitalizacion;
    private String observacionDigitalizacion;
    private Integer numeroPaginas;
    private Integer activo;
    private String 	audUsuarioCreacion;
    private Date audFechaCreacion;
    private String	audUsuarioModificacion;
    private Date audFechaModificacion;
    private Integer asignado;
    private String audUsuarioAsignado;
    private Date audFechaAsignado;
}
