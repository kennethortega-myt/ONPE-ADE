package pe.gob.onpe.scebackend.model.dto.reportes;

import lombok.Data;

@Data
public class FiltroPrecisionAsistAutomaDigitacionDto {
    private String esquema;
    private Integer idProceso;
    private Integer idEleccion;
    private Integer idCentroComputo;
    private Integer tipoReporte;
    private String usuario;
    private String proceso;
    private String centroComputo;
    private String centroComputoCod;
    private String eleccion;
}
