package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Getter
@Setter
public class ReporteHistoricoCierreReaperturaRequestDto {
	@Alphanumeric
	private String esquema;
    private String proceso;
    private Long idCentroComputo;
    private String centroComputo;
    @Alphanumeric
    private String codigoCentroComputo;
    private Integer idEstado;
    private String estado;
    @Alphanumeric
    private String usuario;
    private Long idProceso;

    private String correlativo;
}
