package com.example.tachographanalysis.PDF;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreatePDF {
    public static void createPDF(String[] date,String fileName,String... nameOfImage) throws IOException, DocumentException, ParserConfigurationException, SAXException {
        System.out.println("Start");
        Document doc = new Document();
        PdfWriter writer;
        File dir = new File(".\\PDF\\");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File issetPDF=new File(".\\PDF\\" + fileName + ".pdf");
        int licznik=1;
        String tmpName=fileName;
        while (issetPDF.exists()){
            fileName=tmpName+"_"+licznik;
            licznik++;
            issetPDF=new File(".\\PDF\\" + fileName + ".pdf");
        }

        writer = PdfWriter.getInstance(doc,
                new FileOutputStream(".\\PDF\\" + fileName + ".pdf"));
        BaseFont helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED);
        Font polskieFonty=new Font(helvetica,10);
        doc.open();
        for(int i=0;i< date.length;i++){
            doc.add(new Paragraph(date[i],polskieFonty));
        }
        if(nameOfImage.length>0){
            Image image=Image.getInstance(nameOfImage[0]);
            float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin()
                    - doc.rightMargin()) / image.getWidth()) * 100;
            image.scalePercent(scaler);
            doc.add(image);
        }
        doc.close();
        System.out.println("Stop");
    }
}
