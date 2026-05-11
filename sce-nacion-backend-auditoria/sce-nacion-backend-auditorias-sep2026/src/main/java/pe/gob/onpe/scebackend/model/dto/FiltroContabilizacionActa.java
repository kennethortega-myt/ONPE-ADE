package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

@Data
public class FiltroContabilizacionActa {

    private Long   idProceso;
    private String idEleccion;
    private Long   idAmbito;
    private Long   idCentroComputo;
    private String departamento;
    private String provincia;
    private Long   idUbigeo;

}
