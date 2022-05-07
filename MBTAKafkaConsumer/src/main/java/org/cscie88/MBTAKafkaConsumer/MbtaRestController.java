package org.cscie88.MBTAKafkaConsumer;

import java.util.List;
import java.util.Set;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


// Barry Mullan CSCI-E88 Final Project
// The MbtaRestController class implements a rest api endpoint using 
// the spring web rest support

// this annotation makes this class a rest api endpoint
@RestController
public class MbtaRestController {

    // the service instances are used to interact with the redis cache
    @Autowired
    RouteService routeService;
    @Autowired
    VehicleService vehicleService;

    // used to create json data
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // http get endpoint to return a list of route ids
    // eg. http://localhost:8080/routes
    @GetMapping("/routes")
    public List<String> routes() {
        return routeService.findAllRoutes();
    }

    // returns detailed route information for a route id
    // eg. http://localhost:8080/routes/Red
    @GetMapping("/routes/{id}")
    public Route route(@PathVariable String id) {

        // get the list of vehicles for this route id
        Set<String> vehicleIds = routeService.findAllVehicles(id);

        // construct a vehicle object for each vehicle id based on the 
        // data in the redis cache accessed via the VehicleService
        Object[] vehicles = vehicleIds.stream().map(
            i -> new Vehicle(vehicleService.findById(i))
        ).toArray();
        return new Route(id,vehicles);
    }
    
}
