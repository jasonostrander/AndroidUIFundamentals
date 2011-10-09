package com.example;

public class Time {
    
    public String name = "Lap 1";
    public String time = "00:00:00:000";
    
    public Time(String name, long time) {
        this.name = name;
        this.time = Utils.formatElapsedTime(time);
    }
}
