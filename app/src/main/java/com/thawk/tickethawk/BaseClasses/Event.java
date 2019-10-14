package com.thawk.tickethawk.BaseClasses;

public class Event {
    public String title;
    public String dateAndTime;
    public String lowestPrice;
    public String imageURL;
    public String id;
    public String creatorId;
    public String creatorName;

    public Event(String title, String dateAndTime, String lowestPrice, String imageURL, String id, String creatorID, String creatorName){
        this.title = title;
        this.dateAndTime = dateAndTime;
        this.lowestPrice = lowestPrice;
        this.imageURL = imageURL;
        this.id = id;
        this.creatorId = creatorID;
        this.creatorName = creatorName;
    }
}
