package pe.gob.onpe.sceorcbackend.model.dto.stae.nacion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.model.dto.transmision.ActaBaseDto;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
// Esta clase base contendrá las propiedades comunes a las actas de transmisión
public abstract class ActaTransmisibleBaseDto extends ActaBaseDto {

  // Propiedades de Firmas Manuales (asumiendo String para estandarización)
  private String escrutinioFirmaMm1Manual;
  private String escrutinioFirmaMm2Manual;
  private String escrutinioFirmaMm3Manual;
  private String instalacionFirmaMm1Manual;
  private String instalacionFirmaMm2Manual;
  private String instalacionFirmaMm3Manual;
  private String sufragioFirmaMm1Manual;
  private String sufragioFirmaMm2Manual;
  private String sufragioFirmaMm3Manual;

  // Propiedades de CVAs específicos
  private Long cvasAutomatico;
  private Long cvasv1;
  private Long cvasv2;
  private String ilegibleCvas; // Si el tipo es String en las 3 clases, si no, mantener en las hijas
  private String ilegibleCvasv1;
  private String ilegibleCvasv2;

  // Propiedades de Digitalización y Digitaciones (estandarizando a Long si es posible)
  private Long digitalizacionEscrutinio;
  private Long digitalizacionInstalacion;
  private Long digitalizacionSufragio;
  private Long digitalizacionInstalacionSufragio;
  private Long digitacionFirmasAutomatico;
  private Long digitacionFirmasManual;
  private Long digitacionHoras;
  private Long digitacionObserv;
  private Long digitacionVotos;

  // Propiedades de Control (estandarizando a Long si es posible)
  private Long controlDigEscrutinio;
  private Long controlDigInstalacionSufragio;
  private Long controlDigitacion;

  // Otros
  private String estadoActaOriginal;
  private Long idActa; // Estandarizado a idActa
  private Long idActaCeleste;
  private String idCc;
  private String idCcCeleste;
  private Integer tipoTransmision; // Estandarizado a tipoTransmision
  private String verificador;
  private String verificador2;
}