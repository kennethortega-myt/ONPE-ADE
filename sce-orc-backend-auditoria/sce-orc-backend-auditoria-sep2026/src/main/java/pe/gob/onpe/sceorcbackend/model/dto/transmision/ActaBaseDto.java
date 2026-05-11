package pe.gob.onpe.sceorcbackend.model.dto.transmision;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActaBaseDto implements Serializable {

  private static final long serialVersionUID = -546265979484261065L;
  
  private Integer activo;
  private String audFechaCreacion;
  private String audFechaModificacion;
  private String audUsuarioCreacion;
  private String audUsuarioModificacion;
  private Long cvas;
  private String descripcionObservAutomatico;
  private String descripcionObservManual;
  private String digitoChequeoEscrutinio;
  private String digitoChequeoInstalacion;
  private String digitoChequeoSufragio;
  private Long electoresHabiles;
  private String estadoActa;
  private String estadoActaResolucion;
  private String estadoCc;
  private String estadoDigitalizacion;
  private String estadoErrorMaterial;
  private String horaEscrutinioAutomatico;
  private String horaEscrutinioManual;
  private String horaInstalacionAutomatico;
  private String horaInstalacionManual;
  private Long idArchivoEscrutinio;
  private Long idArchivoInstalacionSufragio;
  private Long idCentroComputo;
  private Long idDetUbigeoEleccion;
  private Long idMesa;
  private String numeroCopia;
  private String numeroLote;
  private String observDigEscrutinio;
  private String observDigInstalacionSufragio;
  private String proceso;
  private String tipoLote;
  private Long totalVotos;
  private Long votosCalculados;
}
