package com.ilya.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;
import java.util.List;

/**
 * Created by ilya on 05.10.2016.
 */


public class TOrder {

    private int id;

    private int sum;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy")
    private Date date;

   private List<TItem> items;


    public void setId(int id) {
        this.id = id;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setItems(List<TItem> items) {
        this.items = items;
    }

    public int getId() {

        return id;
    }

    public int getSum() {
        return sum;
    }

    public Date getDate() {
        return date;
    }

    public List<TItem> getItems() {
        return items;
    }
}
