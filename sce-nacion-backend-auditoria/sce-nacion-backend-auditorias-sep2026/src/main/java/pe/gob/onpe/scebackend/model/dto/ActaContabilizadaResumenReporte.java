package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ActaContabilizadaResumenReporte {

    private EncabezadoFiltroContabilizacionActa encabezado;
    private List<ActaContabilizadaReporte> detalleValidos;
    private ActaContabilizadaReporte votosValidos;
    private ActaContabilizadaReporte votosEmitidos;
    private List<ActaContabilizadaReporte> detalleNoValidos;
    private MesasAInstalarDto resumenMesasAInstalar;
    private ActasProcesadasDto resumenActasProcesadas;
    private ActasPorResolverJEEDto resumenActasPorResolverJEE;
    private ActasAnuladasPorResolucionDto resumenActasAnuladasPorResolucion;
    private ActaContabilizadaDetalleDto resumenActasPorProcesar;
    private Integer electoresHabiles;

}
