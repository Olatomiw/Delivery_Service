package com.deliverywindow.deliveryservice.web.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class StructuredResponse {
   private String day;
   List <Window> timeIntervals;

   public StructuredResponse(String day, List<Window> timeIntervals) {
      this.day = day;
      this.timeIntervals = timeIntervals;
   }

   public String getDay() {
      return day;
   }

   public void setDay(String day) {
      this.day = day;
   }

   public List<Window> getTimeIntervals() {
      return timeIntervals;
   }

   public void setTimeIntervals(List<Window> timeIntervals) {
      this.timeIntervals = timeIntervals;
   }

   @Override
   public String toString() {
      return "StructuredResponse{" +
              "day='" + day + '\'' +
              ", timeIntervals=" + timeIntervals +
              '}';
   }
}

