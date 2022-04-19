package com.example.tachographanalysis.analogueAnalysis;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class RotateImage {
    public static Mat RotateImage(Mat src) throws IOException, InterruptedException {
        TemplateMatching.TemplateMatching(src);
        TimeUnit.SECONDS.sleep(2);
        Integer degree = FindMinimumNumber.FindMinimum();
        Mat rotate = Result.result(src, degree+90);
        return rotate;
    }
}
