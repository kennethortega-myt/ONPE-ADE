package pe.gob.onpe.scebackend.model.dto.request.comun;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UbigeoRequestDto {
    private String esquema;
    private Integer idEleccion;
    private Integer idCentroComputo;
    private Integer idAmbitoElectoral;
    private Integer idUbigePadre;
    private String usuario;
}
