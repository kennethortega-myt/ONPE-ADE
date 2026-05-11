package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;

@Data
public class FiltroUbigeoProvinciaDto {

    private String idEleccion;
    private String departamento;
    private Long idAmbito;
    private Long idCentroComputo;
    private Long idProceso;

}
