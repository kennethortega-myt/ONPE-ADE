package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Data;

import java.util.List;

@Data
public class ReporteMesasUbigeoDto {

    private ReporteMesaUbigeoEncabezadoDto encabezado;
    private ReporteMesaUbigeoResumenDto resumen;
    private List<ReporteMesaUbigeoDetalleDto> detalleMesaUbigeo;
    private String cCodiCompu;
}
