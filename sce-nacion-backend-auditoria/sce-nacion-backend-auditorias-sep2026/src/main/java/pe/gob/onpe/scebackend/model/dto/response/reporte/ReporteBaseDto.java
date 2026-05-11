package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteBaseDto {
    private String codigoCentroComputo;
    private String nombreCentroComputo;

    private String codigoAmbitoElectoral;
    private String nombreAmbitoElectoral;

    private String codigoLocalVotacion;
    private String nombreLocalVotacion;

    private String codigoUbigeoUno;
    private String nombreUbigeoUno;
    private String codigoUbigeoDos;
    private String nombreUbigeoDos;
    private String codigoUbigeoTres;
    private String nombreUbigeoTres;
}
