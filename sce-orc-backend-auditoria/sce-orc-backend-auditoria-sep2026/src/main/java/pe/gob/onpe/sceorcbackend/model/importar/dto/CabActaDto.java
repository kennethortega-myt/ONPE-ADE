package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.model.dto.transmision.ActaBaseDto;
import java.util.List;


@Getter
@Setter
public class CabActaDto extends ActaBaseDto {
    private String contentArchivoEscrutinio;
    private String contentArchivoInstalacionSufragio;
    private Long digitalizacionEscrutinio;
    private Long digitalizacionInstalacionSufragio;
    private Long digitacionFirmasAutomatico;
    private Long digitacionFirmasManual;
    private Long digitacionHoras;
    private Long digitacionObserv;
    private Long digitacionVotos;
    private List<DetActaDto> detalleActas;
    private Long escrutinioFirmaMm1Automatico;
    private Long escrutinioFirmaMm2Automatico;
    private Long escrutinioFirmaMm3Automatico;
    private String formatoArchivoEscrutinio;
    private String formatoArchivoInstalacionSufragio;
    private Long id;
    private String ilegibleCvas;
	private Integer solucionTecnologica;
    private Long instalacionFirmaMm1Automatico;
    private Long instalacionFirmaMm2Automatico;
    private Long instalacionFirmaMm3Automatico;
    private Long controlDigEscrutinio;
    private Long controlDigInstalacionSufragio;
    private Long controlDigitacion;
    private Long sufragioFirmaMm1Automatico;
    private Long sufragioFirmaMm2Automatico;
    private Long sufragioFirmaMm3Automatico;
}