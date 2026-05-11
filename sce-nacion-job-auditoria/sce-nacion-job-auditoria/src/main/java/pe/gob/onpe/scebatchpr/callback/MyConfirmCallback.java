package pe.gob.onpe.scebatchpr.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import pe.gob.onpe.scebatchpr.enums.EnviadoEnum;
import pe.gob.onpe.scebatchpr.service.TabPrTransmisionService;

public class MyConfirmCallback implements RabbitTemplate.ConfirmCallback {
	
	private static final Logger LOG = LoggerFactory.getLogger(MyConfirmCallback.class);
	
    private final TabPrTransmisionService tabPrTransmisionService;

    public MyConfirmCallback(TabPrTransmisionService tabPrTransmisionService) {
        this.tabPrTransmisionService = tabPrTransmisionService;
    }

	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		if (ack) {
			if(correlationData != null && correlationData.getId() != null){
				LOG.info("Mensaje colocado en la cola exitosamente: {}", correlationData.getId());
				this.tabPrTransmisionService.actualizarEnviadoPorCorrelativo(correlationData.getId(),
						EnviadoEnum.ENVIADO.getValor());
			}
		} else {
			if (correlationData != null && correlationData.getId() != null) {
				LOG.info("Error en la confirmación del mensaje, no se pudo colocar en la cola: {}", cause);
				this.tabPrTransmisionService.actualizarEnviadoPorCorrelativo(correlationData.getId(),
						EnviadoEnum.SIN_ENVIAR.getValor());
			}
		}
	}
}