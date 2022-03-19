package com.example.tachographanalysis;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class GeneratePDF
{
    public static void main(String args[])
    {
//created PDF document instance
        Document doc = new Document();
        try
        {
//generate a PDF at the specified location
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("C:\\Users\\acer\\Documents\\GitHub\\Tachograph-analysis\\PDF\\plik.pdf"));
            System.out.println("Stworzono plik PDF.");
//opens the PDF
            doc.open();
//adds paragraph to the PDF file
            doc.add(new Paragraph("Jakiś tekst"));
//close the PDF file
            doc.close();
//closes the writer
            writer.close();
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}