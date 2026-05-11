package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvanceDigitalizacionActasReporteDto {

	private String departamento;
    private String provincia;
    private String distrito;
    private String codigoUbigeo;
    private String nombreEleccion;
    private String centroComputo;
    private String codigoCc;
    private String estadoDigitalizacion;
    private String mesa;
    private Integer total;
    
}
