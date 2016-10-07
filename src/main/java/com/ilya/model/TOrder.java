package com.ilya.model;

import java.util.Date;
import java.util.List;

/**
 * Created by ilya on 05.10.2016.
 */


public class TOrder {

    private int id;

    private int summ;

    private Date date;

   private List<TItem> items;


    public void setId(int id) {
        this.id = id;
    }

    public void setSumm(int summ) {
        this.summ = summ;
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

    public int getSumm() {
        return summ;
    }

    public Date getDate() {
        return date;
    }

    public List<TItem> getItems() {
        return items;
    }
}
