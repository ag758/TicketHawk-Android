package com.thawk.tickethawk;

import java.io.Serializable;

public class TicketType implements Serializable{

    public String name = "";
    public int price = 0;

    public TicketType(String name, int price)

    {
        this.name = name;
        this.price = price;
    }
}
