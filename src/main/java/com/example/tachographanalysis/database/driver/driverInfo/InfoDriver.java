package com.example.tachographanalysis.database.driver.driverInfo;

import com.example.tachographanalysis.database.DatabaseConnection;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InfoDriver {
    @FXML
    public Label firstname, lastname, secondname, email, pesel, city, born, country, cardnumber;
    @FXML
    public TableView<Data> dataView;
    @FXML
    public TableColumn<Data, Integer> idDriverCol, sumRoadCol;
    @FXML
    public TableColumn<Data, String> dateWorkCol, dateAddCol, sumWorkCol, sumBreakCol, fileCol, fileTypeCol;
    @FXML
    private ObservableList<Data> dataList = FXCollections.observableArrayList();
    static int idDriver;
    static String firstName;
    static String lastName;
    public static int getIdDriver(int id) {
        return idDriver = id;
    }

    public void initialize() throws SQLException {
        loadDriver();
        infoDriver();
    }

    public void loadDriver() throws SQLException {
        try {
            String query = "SELECT * FROM driver WHERE id='"+idDriver+"'";
            ResultSet queryOutput = DatabaseConnection.exQuery(query);
            firstname.setText(queryOutput.getString("first_name"));
            lastname.setText(queryOutput.getString("last_name"));
            secondname.setText(queryOutput.getString("second_name"));
            email.setText(queryOutput.getString("email"));
            pesel.setText(queryOutput.getString("pesel"));
            city.setText(queryOutput.getString("city"));
            born.setText(queryOutput.getString("born_date"));
            country.setText(queryOutput.getString("country"));
            cardnumber.setText(queryOutput.getString("id_card"));
            firstName=queryOutput.getString("first_name");
            lastName=queryOutput.getString("last_name");
            try {
                if(queryOutput!=null) {
                    queryOutput.close();
                }
            } catch (Exception e) { }
        } catch (Exception e) {
//            System.err.println(e.getMessage());
        }
    }

    public void infoDriver() throws SQLException {
        try {
            dataList = showData.data();
            dateAddCol.setCellValueFactory(new PropertyValueFactory<>("data_add"));
            dateWorkCol.setCellValueFactory(new PropertyValueFactory<>("date_work"));
            sumWorkCol.setCellValueFactory(new PropertyValueFactory<>("sum_work"));
            sumBreakCol.setCellValueFactory(new PropertyValueFactory<>("sum_break"));
            sumRoadCol.setCellValueFactory(new PropertyValueFactory<>("sum_road"));
            fileTypeCol.setCellValueFactory(new PropertyValueFactory<>("file_type"));
            fileCol.setCellValueFactory(new PropertyValueFactory<>("file"));
            dataView.setItems(dataList);
//            System.out.println(dataList);
        } catch (Exception e) {
//            System.err.println(e.getMessage());
        }

    }
    @FXML
    public void generateOsw(MouseEvent mouseEvent) throws DocumentException, IOException {
        System.out.println("Creating PDF");
        String fileName = firstName + "_" + lastName + "_oswiadczenie_";
        Document doc = new Document();
        PdfWriter writer;
        File dir = new File(".\\PDF\\");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File issetPDF=new File(".\\PDF\\" + fileName +".pdf");
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
        doc.add(new Paragraph("Oświadczenie",polskieFonty));
        doc.add(new Paragraph(firstName+" "+lastName+" jest upoważnionwy do jazdy.",polskieFonty));
        doc.close();
        System.out.println("Created PDF");

    }
}
