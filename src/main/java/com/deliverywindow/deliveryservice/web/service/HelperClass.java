package com.deliverywindow.deliveryservice.web.service;

import com.deliverywindow.deliveryservice.web.domain.StructuredResponse;
import com.deliverywindow.deliveryservice.web.domain.Window;
import com.deliverywindow.deliveryservice.web.domain.WindowResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component

public class HelperClass {

    private static final Logger log = LogManager.getLogger(HelperClass.class);
    private static final int MINIMUM_DURATION_MINUTES = 30;

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
                try {
                    String[] times = interval.split("-");

                    if (times.length != 2) {
                        log.warn("Invalid time format: {}", interval);
                        continue;
                    }
                    LocalTime start = LocalTime.parse(times[0].trim());
                    LocalTime end = LocalTime.parse(times[1].trim());
                    int startToMinutes=(start.getHour()*60) + start.getMinute();
                    int endToMinutes = (end.getHour()*60) + end.getMinute();

                    if (endToMinutes<=startToMinutes) {
                        endToMinutes+=1440;
                    }
                    windows.add(new Window(startToMinutes, endToMinutes));
                }
                catch (DateTimeParseException e){
                    log.warn("Invalid time format: {}", interval, e);
                }
                catch (Exception e){
                    log.error("Error parsing schedule: {}", interval, e);
                }
            }
            parsed.put(day,windows);
            System.out.println("parsed "+parsed);
        }
        return parsed;
    }

    public String intersectWindows(List<Window>venue, List<Window>courier){
        List<Window>finalTime = new ArrayList<>();
        for(Window v : venue){
            for(Window c: courier){
                int start = Math.max(v.getStart(),c.getStart());
                int end = Math.min(v.getEnd(),c.getEnd());

                if(end-start>=MINIMUM_DURATION_MINUTES){
                    finalTime.add(new Window(start,end));
                }
            }
        }
        if (finalTime.isEmpty()){
            finalTime.add(new Window(0,0));
        }
        System.out.println("intersection"+ finalTime);
        return finalTime.stream().map(WindowResponse::new).toList().toString();
    }

}
