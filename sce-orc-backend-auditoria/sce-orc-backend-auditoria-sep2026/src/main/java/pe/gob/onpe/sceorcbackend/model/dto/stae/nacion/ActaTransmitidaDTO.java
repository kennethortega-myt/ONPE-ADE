// ActaTransmitidaDTO (ejemplo simplificado, tendrías que ajustar tipos si no estandarizas)
package pe.gob.onpe.sceorcbackend.model.dto.stae.nacion;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ActaTransmitidaDTO extends ActaTransmisibleBaseDto implements Serializable {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private String archivoEscrutinio; // Si el tipo de Archivo es String aquí
  private String archivoInstalacionSufragio; // Si el tipo de Archivo es String aquí
  private List<DetActaTransmitidaDTO> detalle;
  private List<Object> resoluciones;

  private String sufragioFirmaMm1Automatico;
  private String sufragioFirmaMm2Automatico;
  private String sufragioFirmaMm3Automatico;

  private String escrutinioFirmaMm1Automatico;
  private String escrutinioFirmaMm2Automatico;
  private String escrutinioFirmaMm3Automatico;

  private String instalacionFirmaMm1Automatico;
  private String instalacionFirmaMm2Automatico;
  private String instalacionFirmaMm3Automatico;
}