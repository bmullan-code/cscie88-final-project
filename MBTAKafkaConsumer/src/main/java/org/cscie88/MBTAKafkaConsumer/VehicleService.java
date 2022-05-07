package org.cscie88.MBTAKafkaConsumer;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

// Barry Mullan CSCI-E88 Final Project
// The vechicle service is a service interface to our redis cache of 
// vehicle objects. 

@Service
public class VehicleService { 

	private final String VEHICLE_CACHE = "VEHICLE";

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	private HashOperations<String, String, String> hashOperations;

	// This annotation makes sure that the method needs to be executed after 
	// dependency injection is done to perform any initialization.
	@PostConstruct
	private void intializeHashOperations() {
		hashOperations = redisTemplate.opsForHash();
	}

	// Saves a vehicle update json string to the hash.
	public void save(String vehicleId, String vehicleData) {
		hashOperations.put(VEHICLE_CACHE, vehicleId,vehicleData);
	}

	// returns a vehicle json string by its id
	public String findById(final String id) {
		return (String) hashOperations.get(VEHICLE_CACHE, id);
	}

	// Delete vehicle by id operation.
	public void delete(String id) {
		hashOperations.delete(VEHICLE_CACHE, id);
	}
}
