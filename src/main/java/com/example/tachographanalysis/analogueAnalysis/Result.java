package com.example.tachographanalysis.analogueAnalysis;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class Result {
    public static Mat result(Mat source, Integer degree) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat rotImgSRC = new Mat();
        Point rotPointSRC = new Point(source.cols() / 2, source.rows() / 2);;
        Mat rotMatSRC = Imgproc.getRotationMatrix2D(rotPointSRC, degree , 1);
        Imgproc.warpAffine(source, rotImgSRC, rotMatSRC, source.size(), Imgproc.WARP_INVERSE_MAP);
        return rotImgSRC;
    }
}

