package pe.gob.onpe.sceorcbackend.model.dto;



import com.fasterxml.jackson.annotation.JsonFormat;
import pe.gob.onpe.sceorcbackend.utils.DateTimeUtil;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import java.util.Date;


public record RespaldoDTO(String nombreArchivo,
                          @JsonFormat(pattern = SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_SLASHED, timezone = DateTimeUtil.AMERICA_LIMA)
                          Date fechaHora) {}
