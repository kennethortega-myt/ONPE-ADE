package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteRelacionPuestoCeroDto {
    private String nombreCentroComputo;
    private Long idCentroComputo;
    private String codigoCentroComputo;
    private String fechaHoraPuestaCeroCC;
    private String fechaHoraRecepcionCC;
    private Long estado;
}
