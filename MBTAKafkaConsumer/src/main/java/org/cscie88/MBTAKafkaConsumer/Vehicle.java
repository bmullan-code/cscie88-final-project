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
   private String status = "";
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

    public String getCurrentStatus() {
      return ctx.read("$.attributes.current_status").toString();
    }

    public String getCurrentStopSequence() {
      return ctx.read("$.attributes.current_stop_sequence").toString();
    }

    public String getTrip() {
      return ctx.read("$.relationships.trip.data.id");
    }
    public String getStop() {
      return ctx.read("$.relationships.stop.data.id");
    }

    public void setStatus(String status){
       this.status = status;
    }
    public String getStatus(){
       return this.status;
    }

}


// 2022-05-07 23:15:33.605  INFO 1 --- [or-http-epoll-2] o.c.M.MbtaStreamingClientApplication     : Time: 23:15:33.605636 - event: name[update], id [null], content[{"attributes":{"bearing":135,"current_status":"INCOMING_AT","current_stop_sequence":320,"direction_id":1,"label":"3810-3639","latitude":42.33382,"longitude":-71.24534,"occupancy_status":null,"speed":13.1,"updated_at":"2022-05-07T19:15:28-04:00"},"id":"G-10016","links":{"self":"/vehicles/G-10016"},"relationships":{"route":{"data":{"id":"Green-D","type":"route"}},"stop":{"data":{"id":"70162","type":"stop"}},"trip":{"data":{"id":"50977052","type":"trip"}}},"type":"vehicle"}]
// 2022-05-07 23:15:33.605  INFO 1 --- [or-http-epoll-2] o.c.M.MbtaStreamingClientApplication     : Time: 23:15:33.605677 - event: name[update], id [null], content[{"attributes":{"bearing":0,"current_statu

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