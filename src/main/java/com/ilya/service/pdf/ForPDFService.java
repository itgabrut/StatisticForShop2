package com.ilya.service.pdf;

import com.ilya.interceptors.annotations.Audited;
import com.ilya.model.TItem;
import com.ilya.model.TOrder;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by ilya on 10.10.2016.
 */
@Named
@SessionScoped
public class ForPDFService implements Serializable {

    @Audited
    public ByteArrayOutputStream createPDF(List<TOrder> list) throws IOException{

        int sum = 0;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(20, 20, 20, 20);
        document.add(new Paragraph("Orders statistic"));

        String FONT = "C:\\Windows\\Fonts\\Verdana.ttf";
        PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
        PdfFont bold = PdfFontFactory.createFont(FONT,"Cp1251",true);
        Table table = new Table(new float[]{4, 4, 6, 4});
        table.setWidthPercent(100);
        process(table,list.get(0),font,true);
        for(TOrder tOrder : list){
            process(table,tOrder,bold,false);
            sum +=tOrder.getSum();
        }
        document.add(table);
        Paragraph paragraph = new Paragraph();
        paragraph.setFont(bold);
        String summ = "Total: "+String.format("%,d",sum)+" $";
        paragraph.add(summ);
        document.add(paragraph);
        document.close();
        return byteArrayOutputStream;
    }

    public void process(Table table,TOrder tOrder,PdfFont font,boolean isHeader){
        if(isHeader){
            table.addHeaderCell(new Cell().add(new Paragraph().setFont(font).add("Id")));
            table.addHeaderCell(new Cell().add(new Paragraph().setFont(font).add("Date")));
            table.addHeaderCell(new Cell().add(new Paragraph().setFont(font).add("Items")));
            table.addHeaderCell(new Cell().add(new Paragraph().setFont(font).add("Sum")));
        }
        else {
            table.addCell(new Cell().add(new Paragraph().setFont(font).add(String.valueOf(tOrder.getId()))));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String date = simpleDateFormat.format(tOrder.getDate());
            table.addCell(new Cell().add(new Paragraph().setFont(font).add(date)));
            Table table2 = new Table(3);
            table.setWidthPercent(100);
            table2.addHeaderCell(new Cell().add(new Paragraph().setFont(font).add("Name")));
            table2.addHeaderCell(new Cell().add(new Paragraph().setFont(font).add("Quantity")));
            table2.addHeaderCell(new Cell().add(new Paragraph().setFont(font).add("Price, $")));
            process(table2,tOrder.getItems(),font);
            table.addCell(table2);
            table.addCell(new Cell().add(new Paragraph().setFont(font).add(String.format("%,d",tOrder.getSum())+" $")));
        }
    }

     void process(Table table2, List<TItem> tItem, PdfFont font){
        for(TItem item : tItem){
            table2.addCell(new Cell().add(new Paragraph().setFont(font).add(item.getName())));
            table2.addCell(new Cell().add(new Paragraph().setFont(font).add(String.valueOf(item.getQuantity()))));
            table2.addCell(new Cell().add(new Paragraph().setFont(font).add(String.valueOf(item.getPrice()))));
        }
    }
}
