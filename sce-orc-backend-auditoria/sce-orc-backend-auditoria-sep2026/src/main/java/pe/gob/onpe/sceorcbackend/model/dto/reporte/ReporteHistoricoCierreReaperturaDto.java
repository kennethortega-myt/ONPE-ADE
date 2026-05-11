package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReporteHistoricoCierreReaperturaDto {
    private Date fechaCierre;
    private String usuarioCierre;
    private String apellidosNombresCierre;
    private String motivoCierre;
    private Date fechaReapertura;
    private String usuarioReapertura;
    private String apellidosNombresResponsableReapertura;
    private String motivo;
    private Date fechaReanudacion;
    private String usuarioReanudacion;
    private String apellidosNombresReanudacion;
    private String codigoCentroComputo;
}