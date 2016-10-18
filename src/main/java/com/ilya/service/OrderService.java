package com.ilya.service;

import com.ilya.model.TOrder;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ilya on 05.10.2016.
 */
public interface OrderService {

    List<TOrder> getOrders();

    List<TOrder> getLazyList(String username,int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters)throws IOException;

    List<TOrder> getBetweenDate(String w,String s);

    int count(String username,Map<String,Object> map)throws IOException;
}
