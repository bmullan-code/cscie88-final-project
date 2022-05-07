package org.cscie88.MBTAStreamingClient;

import org.springframework.stereotype.Component;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import reactor.core.publisher.Flux;

// Barry Mullan CSCI-E88 Final Project
// The HttpStreamingClient class is the main class in this project and 
// opens a streaming connection to the MBTA streaming end point

@Component
public class HttpStreamingClient {

    private static final Logger logger = LoggerFactory.getLogger(MbtaStreamingClientApplication.class);

    // these value can be overridden with the corresponding environment variables
    @Value("${MBTA_API_ENDPOINT:https://api-v3.mbta.com}")
    String mbtaApiEndpoint;
    @Value("${MBTA_API_KEY}")
    String mbtaApiKey;
    @Value("${MBTA_API_URI:/vehicles}")
    String mbtaApiUri;
    @Value("${MBTA_KAFKA_VEHICLE_TOPIC:cscie88_mbta_vehicles}")
    String mbtaTopicName;

    @Autowired
    KafkaTemplate template;

    // this method in this class will be called (by the spring framework) once the class is constructed
    @PostConstruct
    public void run() {

        // reference kafka headers
        // https://www.confluent.io/blog/5-things-every-kafka-developer-should-know/#tip-5-power-of-record-headers

        // this consumer receives http streaming events from the mbta api and sends them
        // as kafka messages to the topic			
        Consumer < ServerSentEvent < String >> httpConsumer = new Consumer < ServerSentEvent < String >> () {
            public void accept(ServerSentEvent < String > content) {

                logger.info("Time: {} - event: name[{}], id [{}], content[{}] ",
                    LocalTime.now(), content.event(), content.id(), content.data());

                ProducerRecord < String, String > record =
                    new ProducerRecord < > (mbtaTopicName, content.data());

                // add the header of the record event type (ie. Update, Delete etc.)
                record.headers().add("event", content.event().getBytes(StandardCharsets.UTF_8));

                // send to kafka topic
                template.send(record);
            }
        };

        // this consumer is used to add http headers to the api requrest
        Consumer headersConsumer = new Consumer < HttpHeaders > () {
            @Override
            public void accept(HttpHeaders httpHeaders) {
                httpHeaders.add("accept", "text/event-stream");
                httpHeaders.add("x-api-key", mbtaApiKey);
            }
        };

        /**
         * curl -sN -H "accept: text/event-stream" -H "x-api-key: 948fdf59bd114be9b9af1bcfea2d93ad" "https://api-v3.mbta.com/vehicles"
         * create a web client to the mbta api and add the necessary headers (api key & accept)
         * this code is based on the example from this article
         * https://www.baeldung.com/spring-server-sent-events
         */
        WebClient client = WebClient.builder()
            .baseUrl(mbtaApiEndpoint)
            .defaultHeaders(headersConsumer)
            .build();

        ParameterizedTypeReference < ServerSentEvent < String >> type = new ParameterizedTypeReference < ServerSentEvent < String >> () {};

        Flux < ServerSentEvent < String >> eventStream = client.get()
            .uri(mbtaApiUri)
            .retrieve()
            .bodyToFlux(type);

        eventStream.subscribe(
            httpConsumer,
            error -> logger.error("Error receiving SSE: {}", error),
            () -> logger.info("Completed!!!")
        );

        // example
        // 2022-04-23 11:48:45.095  INFO 39107 --- [ctor-http-nio-3] o.c.M.MbtaStreamingClientApplication     : 
        // Time: 11:48:45.095005 - event: name[update], id [null], 
        // content[
        // {"attributes":{"bearing":45,"current_status":"INCOMING_AT","current_stop_sequence":4,
        // "direction_id":1,"label":"3087","latitude":42.2687,"longitude":-71.07989,
        // "occupancy_status":null,"speed":11.4,"updated_at":"2022-04-23T11:48:40-04:00"},
        // "id":"G-10046","links":{"self":"/vehicles/G-10046"},
        // "relationships":{"route":{"data":{"id":"Mattapan","type":"route"}},
        // "stop":{"data":{"id":"70270","type":"stop"}},
        // "trip":{"data":{"id":"50933526","type":"trip"}}},"type":"vehicle"}
        //]
    }

}