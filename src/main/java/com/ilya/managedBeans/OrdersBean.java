package com.ilya.managedBeans;

import com.ilya.model.TOrder;
import com.ilya.service.OrderService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by ilya on 05.10.2016.
 */


//@ManagedBean( name = "orders",eager = true)
//@RequestScoped
public class OrdersBean {


//    @Inject
    private OrderService service;

    private TOrder TOrder;

    private List<TOrder> list;

    public com.ilya.model.TOrder getTOrder() {
        return TOrder;
    }

    public List<com.ilya.model.TOrder> getList() {
        if(list == null) {
            this.list = service.getOrders();
        }
        return list;
    }

    public List<TOrder> refresh(){
        this.list = service.getOrders();
        return list;
    }

//    @PostConstruct
    public void init(){
        TOrder = service.getOrder();
    }


}
