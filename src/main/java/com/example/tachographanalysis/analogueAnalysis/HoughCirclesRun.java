package com.example.tachographanalysis.analogueAnalysis;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;
public class HoughCirclesRun {
    public static JSONObject run(String args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String filename =  args;
        // Load an image
//        System.out.println(filename);
        Mat src = Imgcodecs.imread(filename, Imgcodecs.IMREAD_COLOR);
        // Check if image is loaded fine
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(gray, gray, 5);
        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
                (double)src.width()/2, // change this value to detect circles with different distances to each other
                100.0, 30.0, src.width()/4, src.width()/2); // change the last two parameters
        // (min_radius & max_radius) to detect larger circles
        for (int x = 0; x < circles.cols(); x++) {
            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            // circle center
            Imgproc.circle(src, center, 1, new Scalar(0,100,100), 3, 8, 0 );
            // circle outline
            int radius = (int) Math.round(c[2]);
            Imgproc.circle(src, center, radius, new Scalar(255,0,255), 3, 8, 0 );
            JSONObject jo = new JSONObject();
            jo.put("centerx",center.x);
            jo.put("centery",center.y);
            jo.put("radius",radius);
            return jo;
        }

//        Imgcodecs.imwrite(filename
//                .replace("file:/","")+"test__2_.png",src);
        return null;
    }

}
