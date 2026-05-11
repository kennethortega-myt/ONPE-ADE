package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;
@Getter
@Setter
public class ReporteTotalEnviadaJEERequestDto extends ReporteBaseRequestDto {
    @Alphanumeric
    private String proceso;
    private Integer tipoConsulta;
    private Integer tipoAgrupado;
    @Alphanumeric
    private String usuarioVisualizacion;
    @Alphanumeric
    private String nombreCentroComputo;
    @Alphanumeric
    private String nombreAmbitoElectoral;
    @Alphanumeric
    private String nombreEleccion;
}