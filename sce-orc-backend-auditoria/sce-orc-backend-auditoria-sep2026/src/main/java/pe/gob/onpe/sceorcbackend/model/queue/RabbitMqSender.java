package pe.gob.onpe.sceorcbackend.model.queue;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.dto.queue.*;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantsQueues;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class RabbitMqSender {

    Logger logger = LoggerFactory.getLogger(RabbitMqSender.class);

    private final RabbitTemplate rabbitTemplate;
    private final RabbitAdmin rabbitAdmin;
    private final RabbitListenerEndpointRegistry endpointRegistry;

    public RabbitMqSender(RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin, @Lazy RabbitListenerEndpointRegistry endpointRegistry) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = rabbitAdmin;
        this.endpointRegistry = endpointRegistry;
    }

    public void sendNewActa(NewActa message) {
        rabbitTemplate.convertAndSend(ConstantsQueues.EXCHAGE_DIRECT, ConstantsQueues.ROUTING_KEY_NEW_ACTA, message);
    }

    public void sendNewActaCeleste(NewActa message) {
        rabbitTemplate.convertAndSend(ConstantsQueues.EXCHAGE_DIRECT, ConstantsQueues.ROUTING_KEY_NEW_ACTA_CELESTE, message);
    }

    public void sendProcessActa(ApprovedActa message) {
        rabbitTemplate.convertAndSend(ConstantsQueues.EXCHAGE_DIRECT, ConstantsQueues.ROUTING_KEY_PROCESS_ACTA, message);
    }

    public void sendProcessLeMm(ApprovedLeMm message) {
        if (message.getAbrevDocumento().equals(ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES))
            rabbitTemplate.convertAndSend(ConstantsQueues.EXCHAGE_DIRECT, ConstantsQueues.ROUTING_KEY_PROCESS_LISTA_ELECTORES, message);
        else if (message.getAbrevDocumento().equals(ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_ASISTENCIA))
            rabbitTemplate.convertAndSend(ConstantsQueues.EXCHAGE_DIRECT, ConstantsQueues.ROUTING_KEY_PROCESS_MIEMBROS_MESA, message);
    }

    public void sendProcessActaStae(NewActaStae message) {
        rabbitTemplate.convertAndSend(ConstantsQueues.EXCHAGE_DIRECT, ConstantsQueues.ROUTING_KEY_PROCESS_ACTA_STAE, message);
    }

    public void sendProcessTransmision(TransmisionQueue message) {
        rabbitTemplate.convertAndSend(ConstantsQueues.EXCHAGE_DIRECT, ConstantsQueues.ROUTING_KEY_PROCESS_TRANSMISION, message);
    }

    public void sendProcessTransmisionSend(EnvioTransmisionQueue message) {
        rabbitTemplate.convertAndSend(ConstantsQueues.EXCHAGE_DIRECT, ConstantsQueues.ROUTING_KEY_PROCESS_TRANSMISION_SEND, message);
    }

    public void sendProcessActaPrediction(Object message) {
        rabbitTemplate.convertAndSend(ConstantsQueues.EXCHAGE_DIRECT, ConstantsQueues.ROUTING_KEY_PROCESS_ACTA_PREDICTION, message);
    }

    /**
     * Limpieza completa: detiene los consumidores Java, elimina y recrea todas las colas
     * (incluyendo mensajes unacknowledged de consumidores externos como Python),
     * y reinicia los consumidores Java.
     */
    public void purgeAllQueues() {
        logger.info("Iniciando limpieza de todas las colas de RabbitMQ");

        // 1. Detener todos los listeners Java
        logger.info("Deteniendo consumidores Java de RabbitMQ...");
        for (String id : endpointRegistry.getListenerContainerIds()) {
            try {
                endpointRegistry.getListenerContainer(id).stop();
                logger.info("Consumidor detenido: {}", id);
            } catch (Exception e) {
                logger.error("Error al detener consumidor {}: {}", id, e.getMessage());
            }
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 2. Eliminar y recrear todas las colas (mata ready + unacknowledged de cualquier consumidor)
        forceCleanQueue(ConstantsQueues.NAME_QUEUE_NEW_ACTA, ConstantsQueues.ROUTING_KEY_NEW_ACTA);
        forceCleanQueue(ConstantsQueues.NAME_QUEUE_NEW_ACTA_CELESTE, ConstantsQueues.ROUTING_KEY_NEW_ACTA_CELESTE);
        forceCleanQueue(ConstantsQueues.NAME_QUEUE_PROCESS_ACTA, ConstantsQueues.ROUTING_KEY_PROCESS_ACTA);
        forceCleanQueue(ConstantsQueues.NAME_QUEUE_PROCESS_LISTA_ELECTORES, ConstantsQueues.ROUTING_KEY_PROCESS_LISTA_ELECTORES);
        forceCleanQueue(ConstantsQueues.NAME_QUEUE_PROCESS_MIEBROS_MESA, ConstantsQueues.ROUTING_KEY_PROCESS_MIEMBROS_MESA);
        forceCleanQueue(ConstantsQueues.NAME_QUEUE_PROCESS_ACTA_STAE, ConstantsQueues.ROUTING_KEY_PROCESS_ACTA_STAE);
        forceCleanQueue(ConstantsQueues.NAME_QUEUE_PROCESS_TRANSMISION, ConstantsQueues.ROUTING_KEY_PROCESS_TRANSMISION);
        forceCleanQueue(ConstantsQueues.NAME_QUEUE_PROCESS_TRANSMISION_SEND, ConstantsQueues.ROUTING_KEY_PROCESS_TRANSMISION_SEND);
        forceCleanQueue(ConstantsQueues.NAME_QUEUE_PROCESS_ACTA_PREDICTION, ConstantsQueues.ROUTING_KEY_PROCESS_ACTA_PREDICTION);

        // Limpiar Dead Letter Queues (DLQ)
        forceCleanQueue(ConstantsQueues.NAME_QUEUE_NEW_ACTA + ConstantsQueues.SUFFIX_DLQ, ConstantsQueues.NAME_QUEUE_NEW_ACTA + ConstantsQueues.SUFFIX_DLQ_RT);
        forceCleanQueue(ConstantsQueues.NAME_QUEUE_NEW_ACTA_CELESTE + ConstantsQueues.SUFFIX_DLQ, ConstantsQueues.NAME_QUEUE_NEW_ACTA_CELESTE + ConstantsQueues.SUFFIX_DLQ_RT);
        forceCleanQueue(ConstantsQueues.NAME_QUEUE_PROCESS_ACTA + ConstantsQueues.SUFFIX_DLQ, ConstantsQueues.NAME_QUEUE_PROCESS_ACTA + ConstantsQueues.SUFFIX_DLQ_RT);
        forceCleanQueue(ConstantsQueues.NAME_QUEUE_PROCESS_LISTA_ELECTORES + ConstantsQueues.SUFFIX_DLQ, ConstantsQueues.NAME_QUEUE_PROCESS_LISTA_ELECTORES + ConstantsQueues.SUFFIX_DLQ_RT);
        forceCleanQueue(ConstantsQueues.NAME_QUEUE_PROCESS_MIEBROS_MESA + ConstantsQueues.SUFFIX_DLQ, ConstantsQueues.NAME_QUEUE_PROCESS_MIEBROS_MESA + ConstantsQueues.SUFFIX_DLQ_RT);
        forceCleanQueue(ConstantsQueues.NAME_QUEUE_PROCESS_ACTA_STAE + ConstantsQueues.SUFFIX_DLQ, ConstantsQueues.NAME_QUEUE_PROCESS_ACTA_STAE + ConstantsQueues.SUFFIX_DLQ_RT);
        forceCleanQueue(ConstantsQueues.NAME_QUEUE_PROCESS_ACTA_PREDICTION + ConstantsQueues.SUFFIX_DLQ, ConstantsQueues.NAME_QUEUE_PROCESS_ACTA_PREDICTION + ConstantsQueues.SUFFIX_DLQ_RT);

        // 3. Reiniciar los consumidores Java
        logger.info("Reiniciando consumidores Java de RabbitMQ...");
        for (String id : endpointRegistry.getListenerContainerIds()) {
            try {
                endpointRegistry.getListenerContainer(id).start();
                logger.info("Consumidor reiniciado: {}", id);
            } catch (Exception e) {
                logger.error("Error al reiniciar consumidor {}: {}", id, e.getMessage());
            }
        }

        logger.info("Limpieza de colas completada");
    }

    private static final Set<String> PRIORITY_QUEUES = Set.of(
            ConstantsQueues.NAME_QUEUE_NEW_ACTA,
            ConstantsQueues.NAME_QUEUE_PROCESS_ACTA,
            ConstantsQueues.NAME_QUEUE_PROCESS_LISTA_ELECTORES,
            ConstantsQueues.NAME_QUEUE_PROCESS_MIEBROS_MESA,
            ConstantsQueues.NAME_QUEUE_PROCESS_ACTA_STAE,
            ConstantsQueues.NAME_QUEUE_PROCESS_ACTA_PREDICTION
    );

    private Map<String, Object> priorityArgs() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-max-priority", 10);
        return args;
    }

    private void forceCleanQueue(String queueName, String routingKey) {
        try {
            rabbitAdmin.deleteQueue(queueName);
            Queue queue;
            if (PRIORITY_QUEUES.contains(queueName)) {
                queue = new Queue(queueName, true, false, false, priorityArgs());
            } else {
                queue = new Queue(queueName, true);
            }
            rabbitAdmin.declareQueue(queue);
            DirectExchange exchange = new DirectExchange(ConstantsQueues.EXCHAGE_DIRECT);
            rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey));
            logger.info("Cola '{}' eliminada y recreada con binding '{}'", queueName, routingKey);
        } catch (Exception e) {
            logger.error("Error al limpiar cola '{}': {}", queueName, e.getMessage(), e);
        }
    }
}
