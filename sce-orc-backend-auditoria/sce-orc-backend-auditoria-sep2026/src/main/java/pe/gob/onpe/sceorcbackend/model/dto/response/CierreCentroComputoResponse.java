package pe.gob.onpe.sceorcbackend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import pe.gob.onpe.sceorcbackend.utils.DateTimeUtil;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CierreCentroComputoResponse {
    private Long cierreId;
    private String mensaje;
    @JsonFormat(pattern = SceConstantes.ISO_DATE_TIME_PATTERN, timezone = DateTimeUtil.AMERICA_LIMA)
    private Date fechaCierre;
    private String usuarioCierre;
    private String motivoCierre;
    private Integer correlativo;
}
