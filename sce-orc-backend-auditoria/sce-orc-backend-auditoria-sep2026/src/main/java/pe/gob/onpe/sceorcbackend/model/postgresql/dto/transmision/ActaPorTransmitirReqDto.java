
package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision;

import lombok.*;
import pe.gob.onpe.sceorcbackend.model.dto.stae.nacion.ActaTransmisibleBaseDto;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActaPorTransmitirReqDto extends ActaTransmisibleBaseDto {

	private static final long serialVersionUID = 3515938785184969019L;
	
	private List<DetActaAccionPorTransmitirReqDto> acciones;
    private ArchivoTransmisionReqDto archivoEscrutinio; 
    private ArchivoTransmisionReqDto archivoInstalacionSufragio;
    private List<DetActaPorTransmitirReqDto> detalle;
    private MesaPorTransmitirReqDto mesa;
    private List<DetActaResolucionPorTransmitirReqDto> resoluciones;
    private Long sufragioFirmaMm1Automatico;
    private Long sufragioFirmaMm2Automatico;
    private Long sufragioFirmaMm3Automatico;

    private Long instalacionFirmaMm1Automatico;
    private Long instalacionFirmaMm2Automatico;
    private Long instalacionFirmaMm3Automatico;

    private Long escrutinioFirmaMm1Automatico;
    private Long escrutinioFirmaMm2Automatico;
    private Long escrutinioFirmaMm3Automatico;
    
    private Long solucionTecnologica;
    private Integer asignado;
    
    private List<DetOficioTransmistirReqDto> detallesOficio;
    
    private ActaPorTransmitirReqDto actaCeleste;
}