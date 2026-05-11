package pe.gob.onpe.scebackend.model.dto.reportes;

import java.util.List;
import lombok.Data;

@Data
public class ReporteMesasUbigeoDto {

    private ReporteMesaUbigeoEncabezadoDto encabezado;
    private ReporteMesaUbigeoResumenDto resumen;
    private List<ReporteMesaUbigeoDetalleDto> detalleMesaUbigeo;
    private String cCodiCompu;
}
