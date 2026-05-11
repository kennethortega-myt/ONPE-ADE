package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Data
public class ReporteMesasUbigeoRequestDto {
  private String eleccion;
  @Alphanumeric
  private String esquema;
  @Alphanumeric
  private String centroComputo;
  @Alphanumeric
  private String departamento;
  @Alphanumeric
  private String provincia;
  @Alphanumeric
  private String distrito;
  @Alphanumeric
  private String usuario;
  private String idProceso;
  private String idEleccion;
  private String tipoDocumento;
  private String proceso;
}
