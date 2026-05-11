package pe.gob.onpe.scebackend.model.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.gob.onpe.scebackend.model.dto.response.trazabilidad.ActaHistorialResumenProjection;
import pe.gob.onpe.scebackend.model.dto.response.trazabilidad.ItemHistory;
import pe.gob.onpe.scebackend.model.service.TrazabilidadService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesEstadoActa;

@Service
public class TrazabilidadServiceImpl implements TrazabilidadService {

    Logger logger = LoggerFactory.getLogger(TrazabilidadServiceImpl.class);


    @Override
    public ItemHistory switchItemHistoryByEstado(ActaHistorialResumenProjection item) {

        String estadoEvaluar = item.getEstadoDigitalizacion().concat(item.getEstadoActa()).concat(item.getEstadoCc());

        return switch (estadoEvaluar) {

            case ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE + ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PENDIENTE + ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA ->
                    construirItemHistory(item.getId(),
                            estadoEvaluar, ConstantesComunes.DESC_ACTA_RECIBIDA,
                            String.format("El acta se encuentra digitalizada en el centro de cómputo, por el usuario %s.", item.getUsuarioModificacion()),
                            null,null);

            case ConstantesEstadoActa.ESTADO_ACTA_DIGITALIZADA + ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PENDIENTE + ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA ->
                    construirItemHistory(item.getId(),
                            estadoEvaluar, ConstantesComunes.MSG_TRAZABILIDAD_PROCESO_ACTA_APROBADA_CTRL_DIGTAL,
                            String.format("El acta fue aprobada por el usuario %s.", item.getUsuarioModificacion()),
                            null,null);
            case ConstantesEstadoActa.ESTADO_ACTA_DIGITADA + ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO + ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA ->
                    construirItemHistory(item.getId(),
                            estadoEvaluar, "ACTA EN PRIMERA DIGITACIÓN",
                            String.format("El acta se encuentra en primera digitación, está siendo procesada por el usuario %s.", item.getUsuarioModificacion()),
                            null,null);

            case ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE + ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PENDIENTE + ConstantesEstadoActa.ESTADO_DIGTAL_1ERA_DIGITACION_RECHAZADA ->
                     construirItemHistory(item.getId(),
                        estadoEvaluar, "ACTA RECHAZADA EN DIGITACIÓN DE ACTAS.",
                        String.format("El acta fue rechazada por el usuario por el usuario %s.", item.getUsuarioModificacion()),
                        null,null);
            case ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION + ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO + ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA ->
                    construirItemHistory(item.getId(),
                            estadoEvaluar, "ACTA EN SEGUNDA DIGITACIÓN",
                            String.format("El acta fue rechazada por el usuario por el usuario %s.", item.getUsuarioModificacion()),
                            null,null);
            case ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR + ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO + ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA ->
                    construirItemHistory(item.getId(),
                            estadoEvaluar, "ACTA POR CORREGIR",
                            String.format("El acta se encuentra en un estado por corregir. Las digitaciones del primer verificador (%s) y segundo verificador (%s), no coinciden.",item.getVerificador(), item.getVerificadorv2()),
                            null, null);


            default -> {
                logger.info("Estado no mapeado, acta {}, estados{}",item.getId() ,estadoEvaluar);
                yield null;
            }

        };
    }


    private ItemHistory construirItemHistory(Long idHistorial, String codEstadoActa, String descripcionEstado, String detalle, String fecha, String fechaInicioFin) {
        return ItemHistory.builder()
                .id(idHistorial)
                .codEstadoActa(codEstadoActa)
                .descripcionEstado(descripcionEstado)
                .detalle(detalle)
                .fecha(fecha)
                .fechaInicioFin(fechaInicioFin)
                .build();
    }

}