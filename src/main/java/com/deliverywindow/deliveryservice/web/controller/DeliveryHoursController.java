package com.deliverywindow.deliveryservice.web.controller;

import com.deliverywindow.deliveryservice.web.domain.DailySchedule;
import com.deliverywindow.deliveryservice.web.service.DeliveryIntersection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Placeholder controller for the delivery hours endpoint.
 */
@RestController
public class DeliveryHoursController {

    private final DeliveryIntersection deliveryIntersection;

    public DeliveryHoursController(DeliveryIntersection deliveryIntersection) {
        this.deliveryIntersection = deliveryIntersection;
    }

    @GetMapping("/delivery-hours")
    public ResponseEntity<Map<String, Object>> getDeliveryHours(
            @RequestParam String citySlug,
            @RequestParam String venueId) {
        
        // TODO: Implement actual business logic
        // This is just a placeholder response to make the application startable
        
//        Map<String, String> deliveryHours = new HashMap<>();
//        deliveryHours.put("Monday", "TODO: Implement intersection logic");
//        deliveryHours.put("Tuesday", "TODO: Implement intersection logic");
//        deliveryHours.put("Wednesday", "TODO: Implement intersection logic");
//        deliveryHours.put("Thursday", "TODO: Implement intersection logic");
//        deliveryHours.put("Friday", "TODO: Implement intersection logic");
//        deliveryHours.put("Saturday", "TODO: Implement intersection logic");
//        deliveryHours.put("Sunday", "TODO: Implement intersection logic");
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("delivery_hours", deliveryHours);
//        response.put("message", "This is a placeholder response. Please implement the actual service.");
//        response.put("city_slug", citySlug);
//        response.put("venue_id", venueId);

//      localhost:8080/?citySlug=berlin&venueId=123
        String uri = "http://localhost:8081/venue-service/venues/"+venueId+"/opening-hours";
        RestTemplate restTemplate = new RestTemplate();
        Map forObject = restTemplate.getForObject(uri, Map.class);
        return ResponseEntity.ok(forObject);
    }

    @GetMapping("/courier-service")
    public ResponseEntity<?> getCourierTime(
            @RequestParam String city,
            @RequestParam String venueId){
        DailySchedule body = deliveryIntersection.deliveryInterSection(venueId, city);
        return ResponseEntity.ok(body);
    }
}