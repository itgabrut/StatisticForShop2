package com.ilya.managedBeans;

import com.ilya.model.TOrder;
import com.ilya.service.pdf.ForPDFService;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by ilya on 10.10.2016.
 */
@ManagedBean
@ViewScoped
public class FileDownloadView {

    private StreamedContent file;

    @ManagedProperty(value = "#{lazy}")
    private OrdersLazyTable ordersLazyTable;

    @Inject
    private ForPDFService forPDFService;


    public StreamedContent getFile() {
        return file;
    }

    public OrdersLazyTable getOrdersLazyTable() {
        return ordersLazyTable;
    }

    public void setOrdersLazyTable(OrdersLazyTable ordersLazyTable) {
        this.ordersLazyTable = ordersLazyTable;
    }

    public void load(ActionEvent event)throws IOException{
        List<TOrder> list = ordersLazyTable.getMyList();
        ByteArrayOutputStream baos = forPDFService.createPDF(list);
        ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
        file = new DefaultStreamedContent(bis, "pdf", "downloaded_optimus.pdf");
    }
}
