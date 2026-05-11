package pe.gob.onpe.scebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionNacionRequestDto;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransmisionNacionRequestDto> kafkaListenerContainerFactory(
            ConsumerFactory<String, TransmisionNacionRequestDto> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, TransmisionNacionRequestDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }
}
