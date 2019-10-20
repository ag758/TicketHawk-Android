package com.thawk.tickethawk.BaseClasses;

public class Event {
    public String title;
    public String dateAndTime;
    public String lowestPrice;
    public String imageURL;
    public String id;
    public String creatorId;
    public String creatorName;

    public String unformattedDateAndtime;

    public Event(String title, String dateAndTime, String lowestPrice, String imageURL, String id, String creatorID, String creatorName, String unformatted){
        this.title = title;
        this.dateAndTime = dateAndTime;
        this.lowestPrice = lowestPrice;
        this.imageURL = imageURL;
        this.id = id;
        this.creatorId = creatorID;
        this.creatorName = creatorName;

        this.unformattedDateAndtime = unformatted;
    }
}
