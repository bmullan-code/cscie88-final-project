package org.cscie88.MBTAKafkaConsumer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.springframework.boot.jackson.JsonComponent;

// Barry Mullan CSCI-E88 Final Project
// This class overrides the json serialization for the vehicle 
// class, as we want to customize how the location is written etc.

@JsonComponent
public class VehicleJsonSerializer extends JsonSerializer<Vehicle> {

    @Override
    public void serialize(Vehicle v, JsonGenerator g, 
      SerializerProvider serializerProvider) throws IOException, 
      JsonProcessingException {
 
        g.writeStartObject();
        g.writeStringField("id", v.getId());
        g.writeStringField("route", v.getRoute());
        g.writeStringField("updated", v.getUpdatedAt());
            g.writeFieldName("location");
            g.writeStartObject();
            g.writeStringField("lat", v.getLat());
            g.writeStringField("lon", v.getLon());
            g.writeEndObject();
        g.writeEndObject();
    }
}