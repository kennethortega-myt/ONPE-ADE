package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;

@Data
public class FiltroUbigeoDistritoDto {

    private String idEleccion;
    private String departamento;
    private String provincia;
    private Long idAmbito;
    private Long idCentroComputo;
    private Long idProceso;
}
