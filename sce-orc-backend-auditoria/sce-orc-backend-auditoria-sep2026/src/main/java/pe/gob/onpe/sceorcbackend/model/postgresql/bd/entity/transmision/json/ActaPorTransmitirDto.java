// ActaPorTransmitirDto
package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import pe.gob.onpe.sceorcbackend.model.dto.stae.nacion.ActaTransmisibleBaseDto;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActaPorTransmitirDto extends ActaTransmisibleBaseDto implements Serializable {
    // Propiedades únicas de ActaPorTransmitirDto
    private List<DetActaAccionPorTransmitirDto> acciones;
    private ArchivoTransmisionDto archivoEscrutinio; // Si el tipo de Archivo es ArchivoTransmisionDto aquí
    private ArchivoTransmisionDto archivoInstalacionSufragio; // Si el tipo de Archivo es ArchivoTransmisionDto aquí
    private List<DetActaPorTransmitirDto> detalle;
    private MesaPorTransmitirDto mesa;
    private List<DetActaResolucionPorTransmitirDto> resoluciones;

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
   
    private List<DetOficioTransmistirDto> detallesOficio;
    
    private ActaPorTransmitirDto actaCeleste;

}