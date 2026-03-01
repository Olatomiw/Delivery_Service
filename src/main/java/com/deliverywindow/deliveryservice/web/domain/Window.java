package com.deliverywindow.deliveryservice.web.domain;


public class Window {
    int start;
    int end;

    public Window(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
