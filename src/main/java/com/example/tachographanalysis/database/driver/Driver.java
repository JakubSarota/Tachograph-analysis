package com.example.tachographanalysis.database.driver;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Driver {
    public static class Drivers {
        Integer id;
        String fname, sname, lname, email, city, country, license, born, pesel, card;

        public Drivers(Integer id, String fname, String sname, String lname, String email, String pesel, String city, String born, String country, String card) {
            this.id = id;
            this.fname = fname;
            this.sname = sname;
            this.lname = lname;
            this.email = email;
            this.pesel = pesel;
            this.city = city;
            this.born = born;
            this.country = country;
            this.card = card;
            this.license = license;
        }

        public Drivers() {
            return;
        }


        public Integer getId() { return id; }
        public String getFname() {
            return fname;
        }
        public String getSname() {
            return sname;
        }
        public String getLname() {
            return lname;
        }
        public String getEmail() {
            return email;
        }
        public String getPesel() {
            return pesel;
        }
        public String getCity() {
            return city;
        }
        public String getBorn() {
            return born;
        }
        public String getCountry() {
            return country;
        }
        public String getCard() { return card; }


        public void setId(Integer id) {
            this.id = id;
        }
        public void setFname(String fname) {
            this.fname = fname;
        }
        public void setSname(String sname) {
            this.sname = sname;
        }
        public void setLname(String lname) {
            this.lname = lname;
        }
        public void setEmail(String email) {this.email = email;}
        public void setPesel(String pesel) {this.pesel = pesel;}
        public void setCity(String city) {this.city = city;}
        public void setBorn(String born) {this.born = born;}
        public void setCountry(String country) {this.country = country;}
        public void setCard(String card) {this.card = card;}

        public ObjectProperty<Drivers> driversObjectProperty = new SimpleObjectProperty<>();
        static ObjectProperty<Drivers>driversObjectPropertyEdit = new SimpleObjectProperty<>(new Drivers());


        public static Drivers getDriversObjectPropertyEdit() {
            return driversObjectPropertyEdit.get();
        }

        public static ObjectProperty<Drivers> driversObjectPropertyEditProperty() {
            return driversObjectPropertyEdit;
        }

        public static void setDriversObjectPropertyEdit(Drivers driversObjectPropertyEdit) {
            Drivers.driversObjectPropertyEdit.set(driversObjectPropertyEdit);
        }
    }
}