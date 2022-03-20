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

    public static Mat work(Mat file) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //Source files
        Mat source=null;
        file = source;
        Mat template=null;

        //Gray change colors
        Mat graySRC = new Mat();
        Mat grayTMP = new Mat();

        //Rotated images
        Mat rotImgSRC = new Mat();

        //Variable to path files

//        InputStream is = getClass.getClassLoader().getResourceAsStream("");
        template=Imgcodecs.imread( "./src/main/resources/com/example/tachographanalysis");

        //Mathmethod and output
        Mat outputImage=new Mat();
        int machMethod= Imgproc.TM_CCOEFF;

        Imgproc.cvtColor(source,graySRC, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(template, grayTMP, Imgproc.COLOR_BGR2GRAY);

        for(int i=-45;i<=90;i++) {
            Core.rotate(graySRC, rotImgSRC, i);
            int result_cols = graySRC.cols() - grayTMP.cols() + 1;
            int result_rows = graySRC.rows() - grayTMP.rows() + 1;

            Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
            Imgproc.matchTemplate(graySRC, grayTMP, result, machMethod);

            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
            Point matchLoc;
            matchLoc = mmr.maxLoc;
            Imgproc.rectangle(source, matchLoc, new Point(matchLoc.x + template.cols(),
                    matchLoc.y + template.rows()), new Scalar(124, 252, 0, 3));
        }

        return null;
    }

}
