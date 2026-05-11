package pe.gob.onpe.sceorcbackend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import pe.gob.onpe.sceorcbackend.utils.DateTimeUtil;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import java.util.Date;

@Data
@Builder
public class ReaperturaCentroComputoResponse {
    private Long cierreId;
    private String mensaje;
    @JsonFormat(pattern = SceConstantes.ISO_DATE_TIME_PATTERN, timezone = DateTimeUtil.AMERICA_LIMA)
    private Date fechaReapertura;
    private String usuarioReapertura;
    private Integer correlativo;
}
