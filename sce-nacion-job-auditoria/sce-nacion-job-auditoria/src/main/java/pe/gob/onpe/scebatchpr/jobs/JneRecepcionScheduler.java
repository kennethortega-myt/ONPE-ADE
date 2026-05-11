package pe.gob.onpe.scebatchpr.jobs;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.scebatchpr.service.JneRecepcionService;
import pe.gob.onpe.scebatchpr.service.PuestaCeroService;

@Component
@Slf4j
@RequiredArgsConstructor
public class JneRecepcionScheduler {

    private final JneRecepcionService jneRecepcionService;
    private final PuestaCeroService puestaCeroService;

    @Async
    @Scheduled(fixedDelayString = "${job.recepcion.delay}", initialDelayString = "${job.recepcion.initial-delay}")
    public void reenviarRecepcionesPendientes() {

        log.info("[JneRecepcionScheduler] Inicio ejecución job");

        try {
            if (puestaCeroService.isPuestaCeroActiva()) {
                log.info("[JneRecepcionScheduler] Puesta a cero activa. Job pausado.");
                return;
            }

            log.info("[JneRecepcionScheduler] Ejecutando trigger de reenvío hacia Nación...");

            boolean success = jneRecepcionService.reenviarPendientes();

            if (success) {
                log.info("[JneRecepcionScheduler] Trigger ejecutado correctamente");
            } else {
                log.warn("[JneRecepcionScheduler] Trigger ejecutado con fallo");
            }

        } catch (Exception e) {
            log.error("[JneRecepcionScheduler] Error ejecutando job de reenvío", e);
        }
    }
}
