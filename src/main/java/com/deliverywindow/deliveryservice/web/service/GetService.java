package com.deliverywindow.deliveryservice.web.service;

import com.sun.jdi.InternalException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GetService {

    public GetService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private final RestTemplate restTemplate;

    @Cacheable(value = "venue", key = "#venueId")
    @CircuitBreaker(name = "venue-service")
    public Map<String, Object> getVenue(String venueId){
        try {
            String uri = "http://localhost:8081/venue-service/venues/"+venueId+"/opening-hours";
            System.out.println("Making request to rest1");
            return restTemplate.getForObject(uri, Map.class);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        throw new InternalException();
    }

    @CircuitBreaker(name = "courier-service")
    @Cacheable(value = "courier", key = "#citySlug")
    public Map<String,Object> getCourier(String citySlug){
        String uri = "http://localhost:8081/courier-service/cities/"+citySlug+"/delivery-hours";
        System.out.println("Making request to rest1");
        return restTemplate.getForObject(uri, Map.class);
    }
}
