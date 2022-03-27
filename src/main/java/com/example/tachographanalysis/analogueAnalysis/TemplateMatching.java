package com.example.tachographanalysis.analogueAnalysis;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class TemplateMatching {
    public static void TemplateMatching(Mat source) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String filePath="C:\\Users\\Kubcorz\\Desktop\\pliki\\test1\\";
        Mat rotImgSRC = new Mat();
        String fileTemplate= "src/main/resources/com/example/tachographanalysis/temp.png";
        Mat template=Imgcodecs.imread(fileTemplate, Imgcodecs.IMREAD_GRAYSCALE);
        Point rotPointSRC = new Point(source.cols() / 2, source.rows() / 2);

        PrintStream o = new PrintStream(new File("findMinimum.txt"));
        System.setOut(o);

        for(int i=0;i<=360;i++) {
            Mat rotMatSRC = Imgproc.getRotationMatrix2D(rotPointSRC, i , 1);
            Imgproc.warpAffine(source, rotImgSRC, rotMatSRC, source.size(), Imgproc.WARP_INVERSE_MAP);
            int result_cols = rotImgSRC.cols() - template.cols() + 1;
            int result_rows = rotImgSRC.rows() - template.rows() + 1;
            Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
            Imgproc.matchTemplate(rotImgSRC, template, result, Imgproc.TM_SQDIFF);
            MinMaxLocResult mmr = Core.minMaxLoc(result);
            System.out.println(mmr.minVal+ "  "+i);
        }

        PrintStream o2 = new PrintStream(new File("findMinimumWithoutDegree.txt"));
        System.setOut(o2);

        for(int i=0;i<=360;i++) {
            Mat rotMatSRC = Imgproc.getRotationMatrix2D(rotPointSRC, i , 1);
            Imgproc.warpAffine(source, rotImgSRC, rotMatSRC, source.size(), Imgproc.WARP_INVERSE_MAP);
            int result_cols = rotImgSRC.cols() - template.cols() + 1;
            int result_rows = rotImgSRC.rows() - template.rows() + 1;
            Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
            Imgproc.matchTemplate(rotImgSRC, template, result, Imgproc.TM_SQDIFF);
            MinMaxLocResult mmr = Core.minMaxLoc(result);
            System.out.println(mmr.minVal);
        }

    }
}