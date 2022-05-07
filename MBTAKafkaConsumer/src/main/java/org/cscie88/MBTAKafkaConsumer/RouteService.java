package org.cscie88.MBTAKafkaConsumer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

// Barry Mullan CSCI-E88 Final Project
// The route service is a service interface to our redis cache of 
// route objects. A route object is keyed by a prefix plus the route 
// id and indexes a set of vehicle id's. These represent the current 
// vehicles for this route
@Service
public class RouteService {

	// we add a prefix to the key in REDIS to distinguish it.
	private final String ROUTE_PREFIX = "ROUTE:";

	@Autowired
	RedisTemplate<String, String> redisTemplate;
    private SetOperations setOperations;

	// setup the redis template for set operations
	@PostConstruct
	private void intializeHashOperations() {
        setOperations = redisTemplate.opsForSet();
	}

	// Save operation.
	public void save(String routeId, String vehicleId) {
        // if not already in the set of vechicle id's, add the vehicle id
		// to the set
		if (!setOperations.isMember(ROUTE_PREFIX+routeId, vehicleId))
            setOperations.add( ROUTE_PREFIX+routeId, vehicleId);
	}

    // remove a vehicle from the route set.
	public void deleteVehicle( String routeId, String vehicleId) {
            setOperations.remove(ROUTE_PREFIX+routeId, vehicleId);
    }

	// return the list of all route ids
	public List<String> findAllRoutes() {
		Set<String> keys = redisTemplate.keys("ROUTE:*");
		return keys.stream().map(
			s -> s.replace("ROUTE:","")
		).collect(Collectors.toList());
	}

	// return the set of all vehicle ids for a route
	public Set<String> findAllVehicles(String routeId) {
        Set<String> vehicleIds = setOperations.members(ROUTE_PREFIX+routeId);
        return vehicleIds;
    }
}
