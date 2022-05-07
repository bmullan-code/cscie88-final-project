package org.cscie88.MBTAStreamingClient;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

// Barry Mullan CSCI-E88 Final Project
// The KafkaTopicConfig class is used to create our kafka topic

@Configuration
public class KafkaTopicConfig {
    
    // this value can be overriden by setting the environment variable BOOTSTRAP_SERVER
    @Value("${BOOTSTRAP_SERVERS:broker1:29092}")
    String bootStrapServers;
    // this value can be overriden by setting the environment variable MBTA_KAFKA_VEHICLE_TOPIC
    @Value("${MBTA_KAFKA_VEHICLE_TOPIC:cscie88_mbta_vehicles}")
    String topicName;

    // a kafka admin bean is used to administer kafka, in this case to create our topic
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        return new KafkaAdmin(configs);
    }
    
    // creates the vehicle topic we will send messages on 
    @Bean
    public NewTopic vehiclesTopic() {
        //  return new NewTopic("cscie88_mbta_vehicles", 1, (short) 1);
        return new NewTopic("cscie88_mbta_vehicles", 1, (short) 1);
    }
}