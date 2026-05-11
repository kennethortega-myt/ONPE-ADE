package pe.gob.onpe.scebatchpr.jobs;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pe.gob.onpe.scebatchpr.entities.admin.ConfiguracionProcesoElectoral;
import pe.gob.onpe.scebatchpr.service.ConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebatchpr.service.FirmaDocumentoInstalacionSufragioService;
import pe.gob.onpe.scebatchpr.service.PuestaCeroService;
import pe.gob.onpe.scebatchpr.utils.Constantes;
import pe.gob.onpe.scebatchpr.utils.FechaUtils;

@Component
public class FirmaDocumentoInstalacionSugrafioScheduler {

	Logger logger = LogManager.getLogger(FirmaDocumentoInstalacionSugrafioScheduler.class);
	
	private final AtomicBoolean isRunning = new AtomicBoolean(false);
	
	private final FirmaDocumentoInstalacionSufragioService firmaDocumentoInstalacionSufragioService;
	
	private final ConfiguracionProcesoElectoralService procesoElectoralService;
	
	private final PuestaCeroService puestaCeroService;
	
	@Value("${app.ext.firmadoc.activado}")
	private boolean sendFirmados;
	
	@Value("${spring.jpa.properties.hibernate.default_schema}")
    private String esquema;
	
	public FirmaDocumentoInstalacionSugrafioScheduler(
			FirmaDocumentoInstalacionSufragioService firmaDocumentoInstalacionSufragioService,
			ConfiguracionProcesoElectoralService procesoElectoralService,
			PuestaCeroService puestaCeroService) {
		this.firmaDocumentoInstalacionSufragioService = firmaDocumentoInstalacionSufragioService;
		this.procesoElectoralService = procesoElectoralService;
		this.puestaCeroService = puestaCeroService;
	}
	

	@Async
	@Scheduled(cron = "${cron.firmar-documento-instalacion-sufragio}")
	public void scheduleFirmarDocumentoInstalacionSugrafio() {
		
		logger.info("Se ejecuto el job de firma documento de instalacion sufragio {}", FechaUtils.getFechaActualPeruana());
		
		ConfiguracionProcesoElectoral proceso = this.procesoElectoralService.findByEsquema(esquema);
		
		if(proceso==null){
			logger.info("No hay un esquema con el nombre {} que este configurado en el admin, se ignora la transmision", esquema);
			return;
		}
		
		if (puestaCeroService.isPuestaCeroActiva()) {
			logger.info("Puesta a cero en ejecución. Job de enviar datos y archivos en transmision pausado.");
            return;
        }
		
		if(proceso.getEtapa()!=null && proceso.getEtapa().equals(Constantes.ETAPA_SIN_CARGA)){
			logger.info("En el esquema {} aun no se ha hecho la carga, se ignora la transmision", proceso.getNombreEsquemaPrincipal());
			return;
		}
		
		if (!isRunning.compareAndSet(false, true)) {
            logger.info("El job para firmar un documento de instalacion sufragio aún se está ejecutando. Se omite esta ejecución.");
            return;
        }
		
		try {
			if(this.sendFirmados){
				this.firmaDocumentoInstalacionSufragioService.firmarDocumentos();
			} else {
				logger.info("La funcionalidad para firmar un documento de instalacion sufragio no esta activado");
			}
		} catch (Exception e) {
            logger.error("Se generó un error en la ejecución del job para firmar documento de instalacion sufragio", e);
        } finally {
            isRunning.set(false);
        }
		
		
	}
	
}
