package pe.gob.onpe.sceorcbackend.model.queue.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import pe.gob.onpe.sceorcbackend.model.dto.queue.EnvioTransmisionQueue;
import pe.gob.onpe.sceorcbackend.model.dto.queue.TransmisionQueue;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionDataService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionHttpService;
import pe.gob.onpe.sceorcbackend.model.queue.RabbitMqSender;
import pe.gob.onpe.sceorcbackend.model.service.kafka.producer.TransmisionProducerKafka;
import pe.gob.onpe.sceorcbackend.utils.ConstantsQueues;

@Component
public class TransmisionListener {

	@Autowired
	private ActaTransmisionDataService actaTransmisionDataService;
	
	@Autowired
	private ActaTransmisionHttpService actaTransmisionHttpService;
	
	@Autowired
	private RabbitMqSender rabbitMqSender;
	
	@Autowired
	private TransmisionProducerKafka transmisionProducerService;
	
	@Value("${app.orc.transmision.mq.kafka}")
    private boolean habilitarKafka;
	
	@RabbitListener(queues = ConstantsQueues.NAME_QUEUE_PROCESS_TRANSMISION)
    public void guardar(TransmisionQueue mensaje) {
		EnvioTransmisionQueue x = this.actaTransmisionDataService.guardarTransmisionAndSend(
        		mensaje.getIdActa(), 
        		mensaje.getEstadoEnum(), 
        		mensaje.getUsuario(), 
        		mensaje.getProceso());
		this.rabbitMqSender.sendProcessTransmisionSend(x);
    }
	
	@RabbitListener(queues = ConstantsQueues.NAME_QUEUE_PROCESS_TRANSMISION_SEND)
    public void enviar(EnvioTransmisionQueue mensaje) {
		if(mensaje.isExitoso()){
			if(habilitarKafka){
				this.transmisionProducerService.enviarActa(mensaje);
			} else {
				this.actaTransmisionHttpService.transmitir(
		        		mensaje.getIdTransmision(), 
		        		mensaje.getProceso(),
		        		mensaje.getUsuario());
			}
			
		}
    }
	
}
