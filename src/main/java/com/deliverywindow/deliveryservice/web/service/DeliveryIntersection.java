package com.deliverywindow.deliveryservice.web.service;

import com.deliverywindow.deliveryservice.web.domain.DailySchedule;
import com.deliverywindow.deliveryservice.web.domain.Window;
import com.deliverywindow.deliveryservice.web.domain.WindowResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;



import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class DeliveryIntersection implements ServiceClass {


    private final HelperClass helperClass;
    private final GetService getService;

    public DeliveryIntersection (HelperClass helperClass, GetService getService){
        this.helperClass = helperClass;
        this.getService = getService;
    }


    @Cacheable(value = "intersect", key = "#venueId + '-' + #citySlug")
    @Override
    public Map<String, String> deliveryInterSection(String venueId, String citySlug) {
        CompletableFuture<Map<String, Object>>getVenue= CompletableFuture.supplyAsync(
                ()-> getService.getVenue(venueId)
        );
        CompletableFuture<Map<String,Object>> getCourier= CompletableFuture.supplyAsync(
                ()-> getService.getCourier(citySlug)
        );
        DailySchedule dailySchedule = getVenue.thenCombine(
                getCourier, (venue, courier)->{
                    DailySchedule ds = new DailySchedule(
                            "Daily", (Map<String, String>) venue.get("openingHours"),
                            (Map<String,String>) courier.get("deliveryHours")
                    );
                    return ds;
                }).join();

        HashMap<String, List<Window>> venueMap = (HashMap<String, List<Window>>) helperClass.splitData(dailySchedule.getVenueMap());
        Map<String, List<Window>> courierMap = helperClass.splitData(dailySchedule.getCourierMap());

        Set<String> allDays = new HashSet<>();
        allDays.addAll(venueMap.keySet());
        allDays.addAll(courierMap.keySet());

        Map<String, String>finalSchedule = new HashMap<>();
        for(String day: allDays){
            List<Window> venueWindows =
                    venueMap.getOrDefault(day, List.of());
            List<Window> courierWindows =
                    courierMap.getOrDefault(day, List.of());
            String s = helperClass.intersectWindows(venueWindows, courierWindows);
            finalSchedule.put(day, s);
        }
        System.out.println(finalSchedule);
        return finalSchedule;
    }


}
