package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import pe.gob.onpe.sceorcbackend.model.service.RespaldoService;
import pe.gob.onpe.sceorcbackend.rest.controller.RespaldoController;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class JobLimpiezaBackupScheduler implements SchedulingConfigurer {

    Logger log = LoggerFactory.getLogger(RespaldoController.class);

    private final RespaldoService respaldoService;

    public JobLimpiezaBackupScheduler(RespaldoService respaldoService) {
        this.respaldoService = respaldoService;
    }


    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        taskRegistrar.setScheduler(limpiezaBackupExecutor());

        taskRegistrar.addTriggerTask(
                this::ejecutarLimpiezaBackups,
                triggerContext ->
                        Instant.now().plus(60, ChronoUnit.MINUTES)
        );
    }

    @Bean(destroyMethod = "shutdown", name = "schedulerLimpiezaBackupExecutor")
    public ScheduledExecutorService limpiezaBackupExecutor() {
        return Executors.newSingleThreadScheduledExecutor(
                r -> new Thread(r, "scheduler-limpieza-backup")
        );
    }

    private void ejecutarLimpiezaBackups() {
        log.info("Inicio job limpieza de backups");
        try {
            respaldoService.eliminarBackupsMasAntiguos(ConstantesComunes.CANTIDAD_BORRAR_BACKUP);
            log.info("Fin job limpieza de backups");
        } catch (Exception e) {
            log.error("Error en job limpieza de backups", e);
        }
    }
}