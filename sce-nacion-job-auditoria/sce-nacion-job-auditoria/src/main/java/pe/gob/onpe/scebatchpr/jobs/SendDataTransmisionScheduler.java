package pe.gob.onpe.scebatchpr.jobs;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import pe.gob.onpe.scebatchpr.service.ConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebatchpr.service.PuestaCeroService;
import pe.gob.onpe.scebatchpr.service.TransmisionMqService;
import pe.gob.onpe.scebatchpr.utils.Constantes;
import pe.gob.onpe.scebatchpr.entities.admin.ConfiguracionProcesoElectoral;

@Component
public class SendDataTransmisionScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(SendDataTransmisionScheduler.class);
	
	private final AtomicBoolean isRunning = new AtomicBoolean(false);

	private TransmisionMqService transmisionMqService;
	
	private PuestaCeroService puestaCeroService;
	
	@Value("${app.sce-batch-pr.transmision.method.mq}")
	private boolean envioMq;
	
	@Value("${spring.jpa.properties.hibernate.default_schema}")
    private String esquema;
	
	@Autowired
	private ConfiguracionProcesoElectoralService procesoElectoralService;
	
	public SendDataTransmisionScheduler(
		TransmisionMqService transmisionMqService,
		PuestaCeroService puestaCeroService
			){
		this.transmisionMqService = transmisionMqService;
		this.puestaCeroService = puestaCeroService;
	}

	/**
	 * <p>
	 * miniute, hour, day(month), month, day(week)
	 * </p>
	 */
	@Async
	@Scheduled(cron = "${cron.expression}")
	public void scheduleTaskWithCronExpressionData() {
		LOG.info("Method executed. Current time is data = {}", new Date());
		
        if (!isRunning.compareAndSet(false, true)) {
            LOG.warn("Job anterior aun en ejecución de transmision de datos a pr, saltando esta iteración");
            return;
        }
		 
		try {
			
			ConfiguracionProcesoElectoral proceso = this.procesoElectoralService.findByEsquema(esquema);
			
			if(proceso==null){
				LOG.info("No hay un esquema con el nombre {} que este configurado en el admin, se ignora la transmision", esquema);
				return;
			}
			
			if (puestaCeroService.isPuestaCeroActiva()) {
				LOG.info("Puesta a cero en ejecución. Job de enviar datos y archivos en transmision pausado.");
	            return;
	        }
			
			if(proceso.getEtapa()!=null && proceso.getEtapa().equals(Constantes.ETAPA_SIN_CARGA)){
				LOG.info("En el esquema {} aun no se ha hecho la carga, se ignora la transmision", proceso.getNombreEsquemaPrincipal());
				return;
			}
			
			if(envioMq) {
				LOG.info("Se realiza el envio de data por MQ");
				this.transmisionMqService.enviarTramaSce();
			}
		} catch (IOException e) {
			LOG.info("Error de IOException", e);
		} catch (InterruptedException e) {
			LOG.info("Error de InterruptedException", e);
		} finally {
			isRunning.set(false); // Liberar siempre
		}

		
	}
	
}
