package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Data
public class FiltroOrganizacionesPoliticasDto {

  @Alphanumeric
  private String schema;
  private Integer idProceso;
  private Integer idEleccion;
  private String centroComputo;
  private String proceso;
  @Alphanumeric
  private String usuario;
  private Integer idCentroComputo;
  private String ccDescripcion;
}
