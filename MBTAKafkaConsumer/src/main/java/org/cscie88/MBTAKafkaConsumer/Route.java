package org.cscie88.MBTAKafkaConsumer;

// Barry Mullan CSCI-E88 Final Project
// The Route class is a convenience class to represent the information 
// aobut a route, specifically its id, and the list of Vehicles currently 
// on that route. 

public class Route {

    private String id = null;
    private Object[] vehicles = null;

    public Route() { 

    }
    public Route(String id, Object[] vehicles) {
        this.id = id;
        this.vehicles = vehicles;
    }

    public String getId() { 
        return this.id;
    }
    public Object[] getVehicles() { 
        return this.vehicles;
    }
    
}
