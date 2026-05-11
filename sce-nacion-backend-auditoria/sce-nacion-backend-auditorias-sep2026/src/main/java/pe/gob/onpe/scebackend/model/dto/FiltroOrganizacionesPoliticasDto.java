package pe.gob.onpe.scebackend.model.dto;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;

@Getter
@Setter
public class FiltroOrganizacionesPoliticasDto {

	@Alphanumeric
    private String schema;
    private Integer idProceso;
    private Integer idEleccion;
    @Alphanumeric
    private String centroComputo;
    private String proceso;
    @Alphanumeric
    private String usuario;
    private Integer idCentroComputo;
    private String ccDescripcion;
}
