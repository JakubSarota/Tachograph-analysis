package com.example.tachographanalysis.analogueAnalysis;

import javafx.scene.image.ImageView;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

public class CropWork {
    public static Mat crop(Mat file) {
        Rect roi = new Rect(0,142, file.width(), file.height()-178);
        Mat cropped = new Mat(file, roi);
        return cropped;
    }

    public static JSONObject work(String file) {


        return null;
    }

}
