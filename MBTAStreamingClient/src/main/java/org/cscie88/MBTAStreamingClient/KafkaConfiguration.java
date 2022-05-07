package org.cscie88.MBTAStreamingClient;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

// Barry Mullan CSCI-E88 Final Project
// The KafkaConfiguration class configures the spring framework
// kafka template which is used to send/receive messages from/to 
// a kafka broker

@Configuration
public class KafkaConfiguration {

    // this value can be overriden by setting the environment variable BOOTSTRAP_SERVER
    @Value("${BOOTSTRAP_SERVERS:broker1:29092}")
    String bootStrapServers;
    
    // a producer factory is used to create a producer for producing kafka messages
    @Bean
    public ProducerFactory<String, String> producerFactoryString() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // creates a kafkatemplate based on our producer factory.
    @Bean
    public KafkaTemplate<String, String> kafkaTemplateString() {
        return new KafkaTemplate<>(producerFactoryString());
    }
}