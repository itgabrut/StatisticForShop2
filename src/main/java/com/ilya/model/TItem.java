package com.ilya.model;


import com.fasterxml.jackson.annotation.JacksonAnnotation;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ilya on 05.10.2016.
 */


public class TItem {

    private String name;

    private int quantity;

    private int price;


    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {

        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }
}
