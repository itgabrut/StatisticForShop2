package com.ilya.service;

import com.ilya.model.TOrder;

import java.util.List;

/**
 * Created by ilya on 05.10.2016.
 */
public interface OrderService {

    List<TOrder> getOrders();

    TOrder getOrder();
}
