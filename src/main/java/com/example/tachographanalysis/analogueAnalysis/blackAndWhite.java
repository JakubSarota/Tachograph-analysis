package com.example.tachographanalysis.analogueAnalysis;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.nio.ByteBuffer;


public class blackAndWhite {

    public static WritableImage loadImage(Image image) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String file = image.getUrl();
        Mat imageFile = Imgcodecs.imread(file);
        System.out.println(imageFile);
        return null;
    }

    public static Mat imageToMat(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        byte[] buffer = new byte[width * height * 4];

        PixelReader reader = image.getPixelReader();
        WritablePixelFormat<ByteBuffer> format = WritablePixelFormat.getByteBgraInstance();
        reader.getPixels(0, 0, width, height, format, buffer, 0, width * 4);

        Mat mat = new Mat(height, width, CvType.CV_8UC4);
        mat.put(0, 0, buffer);
        System.out.println(width+ " " + height);
        return mat;
    }

    public static String source(Image image) {
        String src = image.getUrl();
        System.out.println(src);
        return null;
    }

}
