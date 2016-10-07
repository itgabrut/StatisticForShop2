package com.ilya.service.pdf;


import com.itextpdf.io.font.CFFFont;
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

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.StringTokenizer;

import static com.itextpdf.io.font.PdfEncodings.IDENTITY_H;
import static com.itextpdf.kernel.pdf.PdfName.BaseFont;


/**
 * Created by ilya on 09.10.2016.
 */
public class PDFService {


    public static void main(String[] args) {

        try {
            String data = "C://Users/ilya/test/pad.txt";
            String dest = "C://Users/ilya/test/dest2.pdf";
            createPdf(dest, data);
//            for(String text : readSmallTextFile(data)){
//                Files.copy(Paths.get(data),Files.newOutputStream(Paths.get(dest), StandardOpenOption.CREATE));
//            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    static List<String> readSmallTextFile(String aFileName) throws IOException {
        Path path = Paths.get(aFileName);
        return Files.readAllLines(path, Charset.forName("UTF8"));
    }

    public static void createPdf(String dest,String DATA) throws IOException {
        //Initialize PDF writer
        PdfWriter writer = new PdfWriter(dest);

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);

        // Initialize document
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(20, 20, 20, 20);

        String FONT = "C:\\Windows\\Fonts\\Verdana.ttf";
        PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
        PdfFont bold = PdfFontFactory.createFont(FONT,"Cp1251",true);
        Table table = new Table(new float[]{4, 4, 6, 4});
        table.setWidthPercent(100);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(DATA),"UTF-8"));
        String line = br.readLine();
        System.out.println(line);
        process(table, line, bold, true);
        while ((line = br.readLine()) != null) {
            process(table, line, bold, false);
            System.out.println(line);
        }
        br.close();
        document.add(table);
        Paragraph paragraph = new Paragraph();
        paragraph.setFont(bold);
        paragraph.add("Это по русски");
        document.add(paragraph);
        //Close document
        document.close();
    }
    public static void process(Table table, String line, PdfFont font, boolean isHeader) {
        StringTokenizer tokenizer = new StringTokenizer(line, ":");
        int i = 0;
//            table.addHeaderCell(new Cell().add(new Paragraph().setFont(font).add(new String("привет"))));
        while (tokenizer.hasMoreTokens()) {
            if (isHeader) {
                table.addHeaderCell(new Cell().add(new Paragraph().setFont(font).add(tokenizer.nextToken())));
            } else {
                if(i==2){
                    Table table2 = new Table(3);
                    table.setWidthPercent(100);
                    table2.addHeaderCell(new Cell().add(new Paragraph().setFont(font).add("Name")));
                    table2.addHeaderCell(new Cell().add(new Paragraph().setFont(font).add("Quantity")));
                    table2.addHeaderCell(new Cell().add(new Paragraph().setFont(font).add("Price")));
                    table2.addCell(new Cell().add(new Paragraph().setFont(font).add("Downer")));
                    table.addCell(table2);
                    i++;
                }
                else {
                    table.addCell(new Cell().add(new Paragraph().setFont(font).add(tokenizer.nextToken())));
                    i++;
                    }

                }
            }
        }
}
