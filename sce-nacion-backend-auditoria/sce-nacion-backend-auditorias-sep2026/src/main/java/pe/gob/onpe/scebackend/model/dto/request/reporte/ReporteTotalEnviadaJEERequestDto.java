package pe.gob.onpe.scebackend.model.dto.request.reporte;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;

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
