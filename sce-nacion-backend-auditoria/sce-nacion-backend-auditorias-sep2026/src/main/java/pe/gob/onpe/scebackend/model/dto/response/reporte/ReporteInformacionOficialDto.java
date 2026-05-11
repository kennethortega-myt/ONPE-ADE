package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteInformacionOficialDto {
    private String ubigeo;
    private String codigoEleccion;
    private String codigoAmbitoElectoral;
    private String codigoCentroComputo;
    private String nivelUbigeoUno;
    private String nivelUbigeoDos;
    private String nivelUbigeoTres;
    private Long electoresHabiles;
    private Long ciudadanoVotaron;
    private Long ciudadanoNoVotaron;
    private Long ausentismoCifras;
    private Double porcentajeParticipacion;
    private Double porcentajeAusentismo;
    private Long idCentroComputo;
    private String ambitoGeografico;
}
