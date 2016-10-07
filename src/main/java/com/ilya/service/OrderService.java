package com.ilya.service;

import com.ilya.model.TOrder;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ilya on 05.10.2016.
 */
public interface OrderService {

    List<TOrder> getOrders();

    TOrder getOrder();

    List<TOrder> getLazyList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters);

    List<TOrder> getBetweenDate(String w,String s);

    int count(Map<String,Object> map);
}
