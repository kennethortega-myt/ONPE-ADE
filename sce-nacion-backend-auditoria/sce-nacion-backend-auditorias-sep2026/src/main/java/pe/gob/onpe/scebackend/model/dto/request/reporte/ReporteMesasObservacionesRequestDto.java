package pe.gob.onpe.scebackend.model.dto.request.reporte;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;

@Getter
@Setter
@ToString
public class ReporteMesasObservacionesRequestDto {
    @Alphanumeric
    private String esquema;
    private Integer idProceso;
    private Integer idEleccion;
    @Alphanumeric
    private String eleccion;
    private Integer idCentroComputo;
    @Alphanumeric
    private String codigoCentroComputo;
    @Alphanumeric
    private String ubigeo;
    @Alphanumeric
    private String proceso;
    @Alphanumeric
    private String usuario;
    
    private String departamento;
    private String provincia;
    private String distrito;
}
