package com.thawk.tickethawk;

import java.io.Serializable;

public class Ticket implements Serializable{

    public String key = "";
    public String eventTitle = "";
    public String ticketType = "";
    public String userName = "";
    public String dateAndTime = "";
    public String location = "";

    public Ticket(String key, String eventTitle, String ticketType, String userName, String dateAndTime, String location) {
        this.key = key;
        this.eventTitle = eventTitle;
        this.ticketType = ticketType;
        this.userName = userName;
        this.dateAndTime = dateAndTime;
        this.location = location;
    }
}
