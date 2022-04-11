package com.example.tachographanalysis.PDF;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.image.WritableImage;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreatePDF {
    public static void createPDF(String[] date, String fileName, String nameOfImage, BarChart... barCharts) throws IOException, DocumentException, ParserConfigurationException, SAXException {
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
        if(nameOfImage!=""){
            Image image=Image.getInstance(nameOfImage);
            float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin()
                    - doc.rightMargin()) / image.getWidth()) * 100;
            image.scalePercent(scaler);
            doc.add(image);
        }
        if(barCharts.length>0){
//            System.out.println(barCharts[0].getLayoutX());
//            double layoutX=  barCharts[0].getLayoutX();
//            double layoutY=  barCharts[0].getLayoutY();
            barCharts[0].setLayoutX(0);
            barCharts[0].setLayoutY(0);
            barCharts[0].setVisible(true);
//            System.out.println(barCharts[0].getLayoutX());
            Scene scene = new Scene(new Group(), barCharts[0].getWidth(), barCharts[0].getHeight());
            ((Group) scene.getRoot()).getChildren().add(barCharts[0]);
            //Saving the scene as image
            WritableImage image = scene.snapshot(null);
            File file = new File(".\\tempChart.png");
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "PNG", file);
            Image image1=Image.getInstance(".\\tempChart.png");
            float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin()
                    - doc.rightMargin()) / image1.getWidth()) * 100;
            image1.scalePercent(scaler);
            doc.add(image1);
            file.deleteOnExit();
//            barCharts[0].setLayoutX(layoutX);
//            barCharts[0].setLayoutY(layoutY);
//            System.out.println(barCharts[0].getLayoutX());
        }
        doc.close();
        System.out.println("Stop");
    }
}
