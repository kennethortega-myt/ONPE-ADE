package pe.gob.onpe.sceorcbackend.model.dto.response.otrosdocumentos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import pe.gob.onpe.sceorcbackend.utils.DateTimeUtil;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtroDocumentoDto implements Serializable {
    private Long idOtroDocumento;
    private String codigoCentroComputo;
    private String numeroDocumento;
    private String abrevTipoDocumento;
    private String nombreTipoDocumento;
    private String estadoDigitalizacion;
    private String descEstadoDigitalizacion ;
    private String estadoDocumento;
    private String descEstadoDocumento;
    private Long idArchivo;
    private String nombreArchivo;
    private Integer activo;
    private Integer numeroPaginas;
    @JsonFormat(pattern = SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH, timezone = DateTimeUtil.AMERICA_LIMA)
    private Date fechaRegistro;
    private Date fechaSceScanner;
    private List<String> listaPaginas;
    private List<DetOtroDocumentoDto>  detOtroDocumentoDtoList;
}
