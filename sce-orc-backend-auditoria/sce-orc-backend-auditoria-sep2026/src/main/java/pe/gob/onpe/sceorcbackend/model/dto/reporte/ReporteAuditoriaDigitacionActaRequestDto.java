package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.utils.ConstantesReportes;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Getter
@Setter
public class ReporteAuditoriaDigitacionActaRequestDto {

  private Long idProceso;
  private Long idAmbito;
  @Alphanumeric
  private String codigoAmbitoElectoral;
  @Alphanumeric
  private String ambitoElectoral;
  private Long idCentroComputo;
  @Alphanumeric
  private String codigoCentroComputo;
  @Alphanumeric
  private String centroComputo;
  @Alphanumeric
  private String departamento;
  @Alphanumeric
  private String provincia;
  @Alphanumeric
  private String distrito;
  @Alphanumeric
  private String idUbigeo;
  @Alphanumeric
  private String mesa;
  @Alphanumeric
  private String proceso;
  @Alphanumeric
  private String eleccion;
  @Alphanumeric
  private String schema;
  private Integer idEleccion;
  @Alphanumeric
  private String codigoEleccion;
  @Alphanumeric
  private String usuario;
  private Integer preferencial;
  @Alphanumeric
  private String acronimo;

  public void validarCampos() {
    if (!tieneMesa()) {
      validarDepartamento();
      validarProvincia();
    }
  }

  private void validarDepartamento() {
    if (ConstantesReportes.CODIGO_UBIGEO_NACION.equals(departamento)) {
      throw new IllegalArgumentException("Se debe seleccionar un departamento");
    }
  }

  private void validarProvincia() {
    if (ConstantesReportes.CODIGO_UBIGEO_NACION.equals(provincia)) {
      throw new IllegalArgumentException("Se debe seleccionar un provincia");
    }
  }

  private boolean tieneMesa() {
    if (mesa == null || mesa.isEmpty()){
      return false;
    }
    return true;
  }
}
