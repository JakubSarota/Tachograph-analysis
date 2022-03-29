package com.example.tachographanalysis.analogueAnalysis;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.InputStream;

public class CropWork {
    public static Mat crop(Mat file) {
        Rect roi = new Rect(0,142, file.width(), file.height()-178);
        Mat cropped = new Mat(file, roi);
        return cropped;
    }
}
