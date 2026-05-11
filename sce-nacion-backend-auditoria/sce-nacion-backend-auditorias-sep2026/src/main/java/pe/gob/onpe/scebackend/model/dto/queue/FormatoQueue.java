package pe.gob.onpe.scebackend.model.dto.queue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormatoQueue implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

    private Integer correlativo;
    private Integer tipoFormato;
    private Integer activo;
    private String  audUsuarioCreacion;
    private Date    audFechaCreacion;
    private String  audUsuarioModificacion;
    private Date    audFechaModificacion;
    private String  accion;
    private String  centroComputo;
    private List<DetActaFormatoQueue> detActaFormatoQueueList;

    @Override
    public String toString() {
        return "FormatoQueue{" +
                "correlativo=" + correlativo +
                ", tipoFormato=" + tipoFormato +
                ", audUsuarioCreacion='" + audUsuarioCreacion + '\'' +
                ", audFechaCreacion=" + audFechaCreacion +
                ", centroComputo='" + centroComputo + '\'' +
                ", accion='" + accion + '\'' +
                '}';
    }

}
