package com.thawk.tickethawk.BaseClasses;

public class Vendor {

    String id;
    String name;
    String pictureURL;
    String ticketCategory;

    public Vendor(String id, String name, String pictureURL, String ticketCategory){
        this.id = id;
        this.name = name;
        this.pictureURL = pictureURL;
        this.ticketCategory = ticketCategory;
    }
}
