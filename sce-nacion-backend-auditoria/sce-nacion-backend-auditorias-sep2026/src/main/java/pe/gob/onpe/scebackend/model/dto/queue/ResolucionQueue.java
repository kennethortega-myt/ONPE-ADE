package pe.gob.onpe.scebackend.model.dto.queue;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResolucionQueue implements Serializable {

    private ArchivoQueue archivoQueue;
    private Integer procedencia;
    private Date fechaResolucion;
    private String numeroExpediente;
    private String numeroResolucion;
    private Integer tipoResolucion;
    private String estadoResolucion;
    private String estadoDigitalizacion;
    private String observacionDigitalizacion;
    private Integer numeroPaginas;
    private Integer activo;
    private List<ActaQueue> actasAsociadasQueueList = new ArrayList<>();
    private ActaQueue actaResuelta;
    private String audUsuarioCreacion;
    private Date audFechaCreacion;
    private String audUsuarioModificacion;
    private Date audFechaModificacion;
    private String accion;
    private String opcion;

    @Override
    public String toString() {
        return "ResolucionQueue{" +
                "fechaResolucion=" + fechaResolucion +
                ", numeroExpediente='" + numeroExpediente + '\'' +
                ", numeroResolucion='" + numeroResolucion + '\'' +
                ", tipoResolucion=" + tipoResolucion +
                ", estadoResolucion='" + estadoResolucion + '\'' +
                ", estadoDigitalizacion='" + estadoDigitalizacion + '\'' +
                ", audUsuarioCreacion='" + audUsuarioCreacion + '\'' +
                ", audFechaCreacion=" + audFechaCreacion +
                ", accion='" + accion + '\'' +
                ", opcion='" + opcion + '\'' +
                '}';
    }

}
