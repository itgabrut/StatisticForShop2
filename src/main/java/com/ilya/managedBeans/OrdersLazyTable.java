package com.ilya.managedBeans;

import com.ilya.model.TOrder;
import com.ilya.service.OrderService;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ilya on 08.10.2016.
 */

@ManagedBean(name = "lazy")
@ViewScoped
public class OrdersLazyTable {

    @Inject
    private OrderService service;

    private LazyDataModel<TOrder> model;

    private List<TOrder> myList;

    private Date filterTripDateFrom;

    private Date filterTripDateTo;

    private int sum;

    public int getSum() {
        return sum;
    }

    public List<TOrder> getMyList() {
        return myList;
    }

    public void setMyList(List<TOrder> myList) {
        this.myList = myList;
    }

    public Date getFilterTripDateFrom() {
        return filterTripDateFrom;
    }

    public Date getFilterTripDateTo() {
        return filterTripDateTo;
    }

    public void setFilterTripDateFrom(Date filterTripDateFrom) {
        this.filterTripDateFrom = filterTripDateFrom;
    }

    public void setFilterTripDateTo(Date filterTripDateTo) {
        this.filterTripDateTo = filterTripDateTo;
    }

    public void setModel(LazyDataModel<TOrder> model) {
        this.model = model;
    }

    public LazyDataModel<TOrder> getModel() {
        return model;
    }

    public void filterTripDateFromSelected(SelectEvent event){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
        setFilterTripDateFrom((Date)event.getObject());
    }

    public void filterTripDateToSelected(SelectEvent event){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String tocheck = format.format(getFilterTripDateFrom());
        List<TOrder> list = service.getBetweenDate(format.format(getFilterTripDateFrom()),format.format(event.getObject()));
        model.setWrappedData(list);
        FacesContext.getCurrentInstance().getAttributes().put("datefilter",true);
    }

    public void summerPrice(){
        List<HashMap> list = (List<HashMap>)this.model.getWrappedData();
        int sum = 0 ;
        for( HashMap tOrder : list){
            sum += (Integer)tOrder.get("sum");
        }
        this.sum = sum;
    }

    @PostConstruct
    public void init() {
        this.model = new LazyDataModel<TOrder>(){
            private static final long    serialVersionUID    = 1L;

                @Override
                public List<TOrder> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    if(FacesContext.getCurrentInstance().getAttributes().containsKey("datefilter")) {
                        FacesContext.getCurrentInstance().getAttributes().remove("datefilter");
                        return (List<TOrder>)this.getWrappedData();
                    }
                    List<TOrder> result = service.getLazyList(first, pageSize, sortField, sortOrder.toString(), filters);
                    model.setRowCount(service.count(filters));
                    this.setWrappedData(result);
                    setMyList(result);
                    return (List<TOrder>)this.getWrappedData();
                }
            };

    }
}
