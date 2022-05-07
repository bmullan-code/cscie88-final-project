package org.cscie88.MBTAKafkaConsumer;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

// Barry Mullan CSCI-E88 Final Project
// The Vehicle class is a convenience class to represent the information 
// aobut a vehicle, it is constructed from the json string returned by the 
// mbta's /vehicle api, and uses jsonpath to access the values in the 
// json document

public class Vehicle {

   private String id = null;
   transient private String json = null;
   transient private DocumentContext ctx = null;
   
   // Intro to jsonpath : https://www.baeldung.com/guide-to-jayway-jsonpath

    public Vehicle(String json) {
        this.json = json;
        this.ctx = JsonPath.parse(this.json);
        this.id = ctx.read("$.id");
    }

    public String getId() {
      //   return ctx.read("$.id");
      return this.id;
    }

    public String getRoute() {
        return ctx.read("$.relationships.route.data.id");
    }

    public String getJson() {
        return this.json;
    }

    public String getLat() {
      return ctx.read("$.attributes.latitude").toString();
    }

    public String getLon() {
      return ctx.read("$.attributes.longitude").toString();
    }

    public String getUpdatedAt() { 
      return ctx.read("$.attributes.updated_at").toString();
    }   
}

// example json

/*

{
   "attributes":{
      "bearing":45,
      "current_status":"INCOMING_AT",
      "current_stop_sequence":4,
      "direction_id":1,
      "label":"3087",
      "latitude":42.2687,
      "longitude":-71.07989,
      "occupancy_status":null,
      "speed":11.4,
      "updated_at":"2022-04-23T11:48:40-04:00"
   },
   "id":"G-10046",
   "links":{
      "self":"/vehicles/G-10046"
   },
   "relationships":{
      "route":{
         "data":{
            "id":"Mattapan",
            "type":"route"
         }
      },
      "stop":{
         "data":{
            "id":"70270",
            "type":"stop"
         }
      },
      "trip":{
         "data":{
            "id":"50933526",
            "type":"trip"
         }
      }
   },
   "type":"vehicle"
}

*/