package pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class DigitizationListResolucionItem {
    private Long id;
    private Long idArchivo;
    private String nombreArchivo;
    private String numeroResolucion;
    private Date fechaRegistro;
    private Integer numeroPaginas;
    private String estadoDigitalizacion;
    private List<String> listaPaginas;
}
