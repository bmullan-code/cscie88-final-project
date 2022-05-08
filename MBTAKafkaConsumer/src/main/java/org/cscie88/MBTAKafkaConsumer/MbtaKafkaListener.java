package org.cscie88.MBTAKafkaConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;

// Barry Mullan CSCI-E88 Final Project
// The MbtaKafkaListener class implements a kafka listener which listens on
// vehicles topic for vehicle update messages.
// based on the message type (event) it will update the redis cache with 
// the new information

@Configuration
public class MbtaKafkaListener {

    @Autowired
    VehicleService vehicleService;

    @Autowired
    RouteService routeService;

    private static final Logger logger = LoggerFactory.getLogger(MbtaKafkaListener.class);

    @Value("${MBTA_KAFKA_VEHICLE_TOPIC:cscie88_mbta_vehicles}")
    String topicName;

    // this method is called to handle an instance of the message from the kafka
    // topic
    private void handle(String event,String message) {

        switch(event) {
            // update the saved vehicle, and add it to the route list
            case "add":
            case "reset":
            case "update" : {
                Vehicle v = new Vehicle(message);
                checkVehicleStatus(v);
                vehicleService.save(v.getId(),v.getJson());
                routeService.save(v.getRoute(), v.getId());
                break;
            }

            //  Event:remove Received Message: {"id":"y1279","type":"vehicle"} from partition: 0
            case "remove" : {
                Vehicle v = new Vehicle(message);
                Vehicle deleteV = new Vehicle(vehicleService.findById(v.getId()));
                routeService.deleteVehicle(deleteV.getRoute(),deleteV.getId());
                vehicleService.delete(deleteV.getId());
                break;
            }
        }
    }

    private void checkVehicleStatus(Vehicle v) {

        try {
            // if vehicle is arriving at a stop, compare updated time to schedule time
            if ( v.getCurrentStatus().compareTo("INCOMING_AT")==0) {
                Schedule schedule = new Schedule(v.getTrip(), v.getStop());
                v.setStatus(schedule.getStatus(v.getUpdatedAt()));
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // this method is called when a message is received from the kafka topic
    @KafkaListener(topics = "cscie88_mbta_vehicles")
    public void listenWithHeaders(
        @Payload String message, 
        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
        @Header(name = "event") String event) {
            logger.info(
                "Event:" + event 
                + " Received Message: " + message
                + " from partition: " + partition);

            handle(event,message);
    }
}
