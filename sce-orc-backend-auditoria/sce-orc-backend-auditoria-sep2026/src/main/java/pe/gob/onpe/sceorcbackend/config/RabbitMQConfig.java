package pe.gob.onpe.sceorcbackend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.gob.onpe.sceorcbackend.utils.ConstantsQueues;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    private Map<String, Object> priorityArgs() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-max-priority", 10);
        return args;
    }

    @Bean
    Queue queueNewActa() {
        String queueName = ConstantsQueues.NAME_QUEUE_NEW_ACTA;
        return new Queue(queueName, true, false, false, priorityArgs());
    }

    @Bean
    Queue queueProcessActa() {
        String queueName = ConstantsQueues.NAME_QUEUE_PROCESS_ACTA;
        return new Queue(queueName, true, false, false, priorityArgs());
    }

    @Bean
    Queue queueProcessListaElectores() {
        String queueName = ConstantsQueues.NAME_QUEUE_PROCESS_LISTA_ELECTORES;
        return new Queue(queueName, true, false, false, priorityArgs());
    }

    @Bean
    Queue queueProcessMiembrosMesa() {
        String queueName = ConstantsQueues.NAME_QUEUE_PROCESS_MIEBROS_MESA;
        return new Queue(queueName, true, false, false, priorityArgs());
    }
    
    @Bean
    Queue queueProcessActaStae() {
        String queueName = ConstantsQueues.NAME_QUEUE_PROCESS_ACTA_STAE;
        return new Queue(queueName, true, false, false, priorityArgs());
    }
    
    @Bean
    Queue queueProcessTransmision() {
        String queueName = ConstantsQueues.NAME_QUEUE_PROCESS_TRANSMISION;
        return new Queue(queueName, true);
    }
    
    @Bean
    Queue queueProcessTransmisionSend() {
        String queueName = ConstantsQueues.NAME_QUEUE_PROCESS_TRANSMISION_SEND;
        return new Queue(queueName, true);
    }

    @Bean
    Queue queueProcessActaPrediction() {
        String queueName = ConstantsQueues.NAME_QUEUE_PROCESS_ACTA_PREDICTION;
        return new Queue(queueName, true, false, false, priorityArgs());
    }

    @Bean
    DirectExchange exchange() {
        String exchange = ConstantsQueues.EXCHAGE_DIRECT;
        return new DirectExchange(exchange);
    }

    @Bean
    Binding bindingNewActa(Queue queueNewActa, DirectExchange exchange) {
        String routingKey = ConstantsQueues.ROUTING_KEY_NEW_ACTA;
        return BindingBuilder.bind(queueNewActa).to(exchange).with(routingKey);
    }

    @Bean
    Binding bindingProcessActa(Queue queueProcessActa, DirectExchange exchange) {
        String routingKey = ConstantsQueues.ROUTING_KEY_PROCESS_ACTA;
        return BindingBuilder.bind(queueProcessActa).to(exchange).with(routingKey);
    }

    @Bean
    Binding bindingProcessListaElectores(Queue queueProcessListaElectores, DirectExchange exchange) {
        String routingKey = ConstantsQueues.ROUTING_KEY_PROCESS_LISTA_ELECTORES;
        return BindingBuilder.bind(queueProcessListaElectores).to(exchange).with(routingKey);
    }

    @Bean
    Binding bindingProcessMiembrosMesa(Queue queueProcessMiembrosMesa, DirectExchange exchange) {
        String routingKey = ConstantsQueues.ROUTING_KEY_PROCESS_MIEMBROS_MESA;
        return BindingBuilder.bind(queueProcessMiembrosMesa).to(exchange).with(routingKey);
    }
    
    @Bean
    Binding bindingProcessActaStae(Queue queueProcessActaStae, DirectExchange exchange) {
        String routingKey = ConstantsQueues.ROUTING_KEY_PROCESS_ACTA_STAE;
        return BindingBuilder.bind(queueProcessActaStae).to(exchange).with(routingKey);
    }
    
    @Bean
    Binding bindingProcessTransmision(Queue queueProcessTransmision, DirectExchange exchange) {
        String routingKey = ConstantsQueues.ROUTING_KEY_PROCESS_TRANSMISION;
        return BindingBuilder.bind(queueProcessTransmision).to(exchange).with(routingKey);
    }
    
    @Bean
    Binding bindingProcessTransmisionSend(Queue queueProcessTransmisionSend, DirectExchange exchange) {
        String routingKey = ConstantsQueues.ROUTING_KEY_PROCESS_TRANSMISION_SEND;
        return BindingBuilder.bind(queueProcessTransmisionSend).to(exchange).with(routingKey);
    }

    @Bean
    Binding bindingProcessActaPrediction(Queue queueProcessActaPrediction, DirectExchange exchange) {
        String routingKey = ConstantsQueues.ROUTING_KEY_PROCESS_ACTA_PREDICTION;
        return BindingBuilder.bind(queueProcessActaPrediction).to(exchange).with(routingKey);
    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public org.springframework.amqp.rabbit.core.RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new org.springframework.amqp.rabbit.core.RabbitAdmin(connectionFactory);
    }
}
