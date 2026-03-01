package com.deliverywindow.deliveryservice.web.domain;

import java.util.List;
import java.util.Map;

public class DailySchedule {
    String day;
    Map<String, String> venueMap;
    Map<String, String> courierMap;

    public DailySchedule(String day, Map<String, String> venueMap, Map<String, String> courierMap) {
        this.day = day;
        this.venueMap = venueMap;
        this.courierMap = courierMap;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Map<String, String> getVenueMap() {
        return venueMap;
    }

    public void setVenueMap(Map<String, String> venueMap) {
        this.venueMap = venueMap;
    }

    public Map<String, String> getCourierMap() {
        return courierMap;
    }

    public void setCourierMap(Map<String, String> courierMap) {
        this.courierMap = courierMap;
    }

    @Override
    public String toString() {
        return "DailySchedule{" +
                "day='" + day + '\'' +
                ", venueMap=" + venueMap +
                ", courierMap=" + courierMap +
                '}';
    }
}
