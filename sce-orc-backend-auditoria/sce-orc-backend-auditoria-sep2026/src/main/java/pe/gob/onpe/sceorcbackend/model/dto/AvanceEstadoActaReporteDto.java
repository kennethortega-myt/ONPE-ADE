package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class AvanceEstadoActaReporteDto {

    private EncabezadoFiltroAvanceEstadoActaDto encabezado;
    private AvanceEstadoActaResumenDto resumen;
    private List<AvanceEstadoActaDetalleDto> detalleAvanceEstadoMesa;
    private List<AvanceEstadoActaDetalleDto> totalAvanceEstadoMesa;

}
