package pe.gob.onpe.scebackend.model.dto.request.reporte;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReporteTransaccionesRealizadasRequestDto extends ReporteBaseRequestDto{
    private String proceso;
    private Date fechaDesde;
    private String horaDesde;
    private Date fechaHasta;
    private String horaHasta;
    private Boolean soloConAutorizacion;
    private String usuarioVisualizacion;
    private String nombreCentroComputo;
}
