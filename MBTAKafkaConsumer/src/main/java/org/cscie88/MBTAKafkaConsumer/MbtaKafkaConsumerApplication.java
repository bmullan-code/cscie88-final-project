package org.cscie88.MBTAKafkaConsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// Barry Mullan CSCI-E88 Final Project
// The MbtaKafkaConsumerApplication class is the main spring boot class

@SpringBootApplication
@EnableScheduling
public class MbtaKafkaConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MbtaKafkaConsumerApplication.class, args);
	}

}
