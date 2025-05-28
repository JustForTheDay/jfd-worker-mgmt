package com.jfd.worker.mgmt.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for Kafka Producer.
 * Defines how Kafka messages are produced and sent.
 */
@Configuration // Marks this class as a Spring configuration class
public class KafkaProducerConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}") // Injects Kafka bootstrap servers from properties
    private String bootstrapServers;

    /**
     * Configures the Kafka ProducerFactory.
     * This factory creates Producer instances.
     *
     * @return A ProducerFactory configured with necessary properties.
     */
    @Bean // Marks this method as a bean definition
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // Serializer for message keys
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // Serializer for message values
        // You can add more producer-specific configurations here, e.g., acks, retries, batch size etc.
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Configures the KafkaTemplate.
     * KafkaTemplate is a high-level abstraction for sending messages to Kafka topics.
     *
     * @return A KafkaTemplate instance.
     */
    @Bean // Marks this method as a bean definition
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
