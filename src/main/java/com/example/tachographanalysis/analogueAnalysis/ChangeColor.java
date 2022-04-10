package com.example.tachographanalysis.analogueAnalysis;

import org.json.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;

public class ChangeColor {
    public BufferedImage im=null;
    private WritableRaster raster=null;
    private int width = 0;
    private int height = 0;
    private int pixels[] = new int[3];
    private int pixels2[] = new int[3];
    private int pixels3[] = new int[3];

    public ChangeColor(BufferedImage fileName) throws IOException {
        im=fileName;
        raster = im.getRaster();
        width = raster.getWidth();
        height = raster.getHeight();
    }

    public ChangeColor(Image fileName) {
        im= (BufferedImage) fileName;
        raster = im.getRaster();
        width = raster.getWidth();
        height = raster.getHeight();
    }

    public void changeColor(int color){
        Random r = new Random();
        int ww[]=new int[3];

        if(color>3 || color<1)
            throw new IllegalArgumentException("Unsupported argument. Value must be <1:3>");
        for(int i=0;i<width;i++) {
            for(int j=0;j<height;j++) {
                raster.getPixel(i, j, pixels);
                ww[0] = r.nextInt((int)((color==1?1:0)*pixels[0])+1);
                ww[1] = r.nextInt((int)((color==2?1:0)*pixels[1])+1);
                ww[2] = r.nextInt((int)((color==3?1:0)*pixels[2])+1);
                raster.setPixel(i, j, ww);
            }
        }
    }

    public void greyScale(){
        double ww[]=new double[3];
        for(int i=0;i<width;i++) {
            for(int j=0;j<height;j++) {
                raster.getPixel(i, j, pixels);
                ww[0] = 0.299*pixels[0]+0.587*pixels[1]+0.114*pixels[2];
                ww[1] = 0.299*pixels[0]+0.587*pixels[1]+0.114*pixels[2];
                ww[2] = 0.299*pixels[0]+0.587*pixels[1]+0.114*pixels[2];
                raster.setPixel(i, j, ww);
            }
        }
    }
    public void sepia(){
        double ww[]=new double[3];
        this.greyScale();
        for(int i=0;i<width;i++) {
            for(int j=0;j<height;j++) {
                raster.getPixel(i, j, pixels);
                ww[0] = (pixels[0] * 0.393 + pixels[1] * 0.769 + pixels[2] * 0.189 ) / 1.351;
                ww[1] = (pixels[0] * 0.349 + pixels[1] * 0.686 + pixels[2] * 0.186 ) / 1.203;
                ww[2] = (pixels[0] * 0.272 + pixels[1] * 0.534 + pixels[2] * 0.131 ) / 2.140;
                raster.setPixel(i, j, ww);
            }
        }
    }

    public void brightness(int howMore) throws IllegalAccessException {
        if(howMore<0)
            throw new IllegalAccessException("Only positive values!");
        double hsv[];
        double ww[];
        for(int i=0;i<width;i++) {
            for(int j=0;j<height;j++) {
                raster.getPixel(i, j, pixels);
                hsv=rgb2hsv(pixels[0], pixels[1], pixels[2]);
                hsv[2]=hsv[2]+howMore>240?240:hsv[2]+howMore;
                ww=hsv2rgb(hsv[0], hsv[1], hsv[2]);
                raster.setPixel(i, j, ww);
            }
        }
    }

    public void exaggeration(int h, int s) throws IllegalArgumentException {
        if(h<0 || s<0)
            throw new IllegalArgumentException("Only positive values!");
        if(h>360 || s>240)
            throw new IllegalArgumentException("H must be <0:360>, S<0:240>");
        double hsv[];
        double ww[];
        for(int i=0;i<width;i++) {
            for(int j=0;j<height;j++) {
                raster.getPixel(i, j, pixels);
                hsv=rgb2hsv(pixels[0], pixels[1], pixels[2]);
                hsv[0]=hsv[0]+h>360?360:hsv[0]+h;
                hsv[1]=hsv[1]+s>240?240:hsv[1]+s;
                ww=hsv2rgb(hsv[0], hsv[1], hsv[2]);
                raster.setPixel(i, j, ww);
            }
        }
    }

    public void save(String type, String fileName) throws IOException{
        ImageIO.write(im,type,new File(fileName));
    }
    public void petla_po_pikselach(){
        String test="";
        for(int i=0;i<width;i++) {
            for(int j=0;j<height;j++) {
                    raster.getPixel(i, j, pixels);
                    if(pixels[0]<245&&pixels[1]<245&&pixels[2]<245){
                        int black[]={0,0,0};
                        raster.setPixel(i, j, black);
                    }else{
                        int black[]={255,255,255};
                        raster.setPixel(i, j, black);
                    }
//                        System.out.println(pixels[0]+" "+pixels[1]+" "+pixels[2]);
            }
        }
    }
    public JSONObject czas_pracy(){
        JSONObject json=new JSONObject();
        double coMinuty=(double)width/(24*60);
        List<String> minuty = new ArrayList<String>();
        for(int i=0;i<width;i++) {
            int j= (int) (height*0.765);
            raster.getPixel(i, j, pixels);
//            System.out.println(pixels[0]);
            if(pixels[0]==255){
                raster.getPixel(i, j-10, pixels2);
                if(pixels2[0]==255) {
                    raster.getPixel(i, j+10, pixels3);
                    if(pixels3[0]==255) {
                        minuty.add(String.valueOf((int) (i/coMinuty)));

                    }
                }
            }
            int black[]={100,200,50};
            raster.setPixel(i, j, black);
            raster.setPixel(i, j-10, black);
            raster.setPixel(i, j+10, black);
            json.put("praca",minuty);

        }
        return json;
    }
    public String ktoraGodzina(int m){
        int godziny=m/60;
        int minuty=m%60;
        String mm= String.valueOf(minuty);
        if(minuty<10)
            mm="0"+minuty;
        return godziny+":"+mm;
    }
    private double[] hsv2rgb(double hue, double sat, double val) {
        double red = 0, grn = 0, blu = 0;
        double i, f, p, q, t;
        double result[] = new double[3];
        if(val==0) {
            red = 0;
            grn = 0;
            blu = 0;
        } else {
            hue/=60;
            i = Math.floor(hue);
            f = hue-i;
            p = val*(1-sat);
            q = val*(1-(sat*f));
            t = val*(1-(sat*(1-f)));
            if (i==0) {red=val; grn=t; blu=p;}
            else if (i==1) {red=q; grn=val; blu=p;}
            else if (i==2) {red=p; grn=val; blu=t;}
            else if (i==3) {red=p; grn=q; blu=val;}
            else if (i==4) {red=t; grn=p; blu=val;}
            else if (i==5) {red=val; grn=p; blu=q;}
        }
        result[0] = red;
        result[1] = grn;
        result[2] = blu;
        return result;
    }

    private double[] rgb2hsv(double red, double grn, double blu) {
        double hue, sat, val;
        double x, f, i;
        double result[] = new double[3];

        x = Math.min(Math.min(red, grn), blu);
        val = Math.max(Math.max(red, grn), blu);
        if (x == val){
            hue = 0;
            sat = 0;
        } else {
            f = (red == x) ? grn-blu : ((grn == x) ? blu-red : red-grn);
            i = (red == x) ? 3 : ((grn == x) ? 5 : 1);
            hue = ((i-f/(val-x))*60)%360;
            sat = ((val-x)/val);
        }
        result[0] = hue;
        result[1] = sat;
        result[2] = val;
        return result;
    }

    public void blackAndWhite(int ton) {
        double hsv[];
        double ww[];
        for(int i=0;i<width;i++) {
            for(int j=0;j<height;j++) {
                raster.getPixel(i, j, pixels);
                hsv=rgb2hsv(pixels[0], pixels[1], pixels[2]);
                if(hsv[2]>ton) {
                    ww=hsv2rgb(hsv[0], hsv[1], hsv[2]);
                    Arrays.fill(ww, 0);
                } else {
                    ww=hsv2rgb(hsv[0], hsv[1], hsv[2]);
                    Arrays.fill(ww, 255);
                }
                raster.setPixel(i, j, ww);
            }
        }
    }

    public void blackAndWhite(int ton, boolean better) {
        double hsv[];
        double ww[];
        Random r = new Random();
        double min=-(0.15*ton);
        double max=0.15*ton;
        int ton2=0;
        this.greyScale();
        for(int i=0;i<width;i++) {
            for(int j=0;j<height;j++) {
                raster.getPixel(i, j, pixels);
                hsv=rgb2hsv(pixels[0], pixels[1], pixels[2]);
                if(better){
                    ton2=ton+(int)(min + (int)(Math.random()  * ((max - min) + 1)));
                } else
                    ton2=ton;
                if(hsv[2]>ton2) {

                    ww=hsv2rgb(hsv[0], hsv[1], hsv[2]);
                    Arrays.fill(ww, 0);
                } else {
                    ww=hsv2rgb(hsv[0], hsv[1], hsv[2]);
                    Arrays.fill(ww, 255);
                }
                raster.setPixel(i, j, ww);
            }
        }
    }
}