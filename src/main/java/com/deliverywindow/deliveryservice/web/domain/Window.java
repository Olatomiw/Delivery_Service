package com.deliverywindow.deliveryservice.web.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public class Window {
    LocalTime start;
    LocalTime end;

    public Window(LocalTime start, LocalTime end){
        this.start = start;
        this.end = end;
    }

    public Window() {
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Window{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
