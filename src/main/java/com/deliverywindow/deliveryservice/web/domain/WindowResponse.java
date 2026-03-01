package com.deliverywindow.deliveryservice.web.domain;


public class WindowResponse {
    private String startTime;
    private String endTime;


    public WindowResponse(Window window) {
        this.startTime = formatTime(window.getStart());
        this.endTime = formatTime(window.getEnd());
    }

    private String formatTime(int minutes) {
        if (minutes < 1) {
            return "closed";
        }
        int normalized = minutes % 1440;
        int hours = normalized / 60;
        int mins = normalized % 60;
        return String.format("%02d:%02d", hours, mins);
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "{" +
                "start='" + startTime + '\'' +
                ", end=" + endTime +
                '}';
    }
}
