package com.deliverywindow.deliveryservice.web.service;

import com.deliverywindow.deliveryservice.web.domain.DailySchedule;
import com.deliverywindow.deliveryservice.web.domain.StructuredResponse;
import com.deliverywindow.deliveryservice.web.domain.Window;
import com.sun.jdi.InternalException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalTime;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class DeliveryIntersection implements ServiceClass {

    private final RestTemplate restTemplate;

    public DeliveryIntersection (RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, List<String>> deliveryInterSection(String venueId, String citySlug) {
        CompletableFuture<Map<String, Object>>getVenue= CompletableFuture.supplyAsync(
                ()-> getVenue(venueId)
        );
        CompletableFuture<Map<String,Object>> getCourier= CompletableFuture.supplyAsync(
                ()-> getCourier(citySlug)
        );
        DailySchedule dailySchedule = getVenue.thenCombine(
                getCourier, (venue, courier)->{
                    DailySchedule ds = new DailySchedule(
                            "Daily", (Map<String, String>) venue.get("openingHours"),
                            (Map<String,String>) courier.get("deliveryHours")
                    );
                    return ds;
                }).join();

        HashMap<String, List<Window>> venueMap = (HashMap<String, List<Window>>) splitData(dailySchedule.getVenueMap());
        Map<String, List<Window>> courierMap = splitData(dailySchedule.getCourierMap());

        Map<String, List<String>>finalSchedule = new HashMap<>();
        for(String day: venueMap.keySet()){
            List<Window> venueWindows = venueMap.get(day);
            List<Window> courierWindows = courierMap.getOrDefault(day, List.of());

            List<String> windows = intersectWindows(venueWindows, courierWindows);
            finalSchedule.put(day, windows);
        }
        System.out.println(finalSchedule);
        return finalSchedule;
    }

    @CircuitBreaker(name = "venue-service")
    @Cacheable(value = "venue")
    public Map<String, Object> getVenue(String venueId){
        try {
            String uri = "http://localhost:8081/venue-service/venues/"+venueId+"/opening-hours";
            return restTemplate.getForObject(uri, Map.class);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        throw new InternalException();
    }

    @CircuitBreaker(name = "courier-service")
    @Cacheable(value = "courier")
    public Map<String,Object> getCourier(String citySlug){
        String uri = "http://localhost:8081/courier-service/cities/"+citySlug+"/delivery-hours";
        return restTemplate.getForObject(uri, Map.class);
    }

    public Map<String, List<Window>> splitData(Map<String,String> rawSchedule) {
        Map<String, List<Window>> parsed = new HashMap<>();
        for (Map.Entry<String, String> entry : rawSchedule.entrySet()) {
            String day = entry.getKey();
            String raw = entry.getValue();
            List<Window> windows = new ArrayList<>();

            if (raw.equalsIgnoreCase("Closed")) {
                StructuredResponse structuredResponse = new StructuredResponse(day, windows);
                parsed.put(day,windows);
                continue;
            }

            String[] intervals = raw.split(",\\s*");
            for (String interval : intervals) {
                String[] times = interval.split("-");
                LocalTime start = LocalTime.parse(times[0].trim());
                LocalTime end = LocalTime.parse(times[1].trim());
                if (end.isBefore(start)) {
                    windows.add(new Window(start, LocalTime.MAX));
                    windows.add(new Window(LocalTime.MIN, end));
                } else {
                    windows.add(new Window(start, end));
                }
            }
            parsed.put(day,windows);
            System.out.println("parsed "+parsed);
        }
        return parsed;
    }

    private List<String> intersectWindows(List<Window>venue, List<Window>courier){
        List<String>finalTime = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        for(Window v : venue){
            for(Window c: courier){
                LocalTime start = v.getStart().isAfter(c.getStart()) ? v.getStart() : c.getStart();
                LocalTime end = v.getEnd().isBefore(c.getEnd()) ? v.getEnd() : c.getEnd();

                if(start.isBefore(end)){
                    if(Duration.between(start,end).toMinutes()>=30){
                        finalTime.add(start.format(formatter)+"-"+ end.format(formatter));
                    }
                }
            }
        }
        if (finalTime.isEmpty()){
            finalTime.add("Closed");
        }
        System.out.println("intersection"+ finalTime);
        return finalTime;
    }
}
