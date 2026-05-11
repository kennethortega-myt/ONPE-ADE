package pe.gob.onpe.scebatchpr.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.ConfirmType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import pe.gob.onpe.scebatchpr.callback.MyConfirmCallback;
import pe.gob.onpe.scebatchpr.service.TabPrTransmisionService;


@Configuration
public class RabbitMQConfig {
	
	Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

	@Value("${app.sce-batch-pr.transmision.send-queue}")
    private String sendQueue;

    @Value("${app.sce-batch-pr.transmision.reply-queue}")
    private String replyQueue;
    
    @Value("${app.sce-batch-pr.transmision.send-queue-file}")
    private String sendQueueFile;

    @Value("${app.sce-batch-pr.transmision.reply-queue-file}")
    private String replyQueueFile;
	
    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host:/}")
    private String virtualHost;
    

    private TabPrTransmisionService tabPrTransmisionService;
    
    public RabbitMQConfig(
    		TabPrTransmisionService tabPrTransmisionService){
    	this.tabPrTransmisionService = tabPrTransmisionService;
    }
    
    @Bean
    CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirmType(ConfirmType.CORRELATED); // Configuración para confirmación
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }
    
    @Bean
    RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
    	logger.info("Confirmaciones de publicacion: {}",connectionFactory.isPublisherConfirms());
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        // Configurar la confirmación del mensaje
        rabbitTemplate.setConfirmCallback(new MyConfirmCallback(tabPrTransmisionService));
        
        rabbitTemplate.setRetryTemplate(retryTemplate());
        return rabbitTemplate;
    }

    @Bean
    RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // Configura la política de reintentos
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3); // Número máximo de intentos de reintento
        retryTemplate.setRetryPolicy(retryPolicy);

        // Configura la política de retroceso (backoff)
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000); // Intervalo inicial de 1 segundo
        backOffPolicy.setMultiplier(2.0); // Factor de multiplicación
        backOffPolicy.setMaxInterval(10000); // Intervalo máximo de 10 segundos
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }
    
	@Bean
    Queue requestQueue() {
        return new Queue(sendQueue);
    }
	
	@Bean
    Queue responseQueue() {
        return new Queue(replyQueue);
    }
	
	@Bean
    Queue requestQueueFile() {
        return new Queue(sendQueueFile);
    }
	
	@Bean
    Queue responseQueueFile() {
        return new Queue(replyQueueFile);
    }
	
	
	
	
}
