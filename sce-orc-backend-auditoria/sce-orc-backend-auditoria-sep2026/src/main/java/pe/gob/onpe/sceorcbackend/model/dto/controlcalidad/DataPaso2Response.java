package pe.gob.onpe.sceorcbackend.model.dto.controlcalidad;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataPaso2Response {

	private Long idImgFirmaPresidenteEsc;
    private Long idImgFirmaSecretarioEsc;
    private Long idImgFirmaTercerMiembroEsc;

    private Long idImgFirmaPresidenteIns;
    private Long idImgFirmaSecretarioIns;
    private Long idImgFirmaTercerMiembroIns;
    
    private Long idImgFirmaPresidenteSuf;
    private Long idImgFirmaSecretarioSuf;
    private Long idImgFirmaTercerMiembroSuf;
    
    private Long idImgObsEscrutinio;
    private Long idImgObsInstalacion;
    private Long idImgObsSufragio;
    
    private boolean isActaConFirma;
}
