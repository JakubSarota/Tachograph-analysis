package com.example.tachographanalysis.analogueAnalysis;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TemplateMatching {
    public static void TemplateMatching(Mat source) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat rotImgSRC = new Mat();
        String fileTemplate = "src/main/resources/com/example/tachographanalysis/temp.png";
        Mat template=Imgcodecs.imread(fileTemplate, Imgcodecs.IMREAD_GRAYSCALE);
        Point rotPointSRC = new Point(source.cols() / 2, source.rows() / 2);

        List<String> findMinimum = new ArrayList<String>();
        List<String> findMinimumWithoutDegree = new ArrayList<String>();

        for(int i=0;i<=360;i=i+2) {
            Mat rotMatSRC = Imgproc.getRotationMatrix2D(rotPointSRC, i , 1);
            Imgproc.warpAffine(source, rotImgSRC, rotMatSRC, source.size(), Imgproc.WARP_INVERSE_MAP);
            int result_cols = rotImgSRC.cols() - template.cols() + 1;
            int result_rows = rotImgSRC.rows() - template.rows() + 1;
            Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
            Imgproc.matchTemplate(rotImgSRC, template, result, Imgproc.TM_SQDIFF);
            MinMaxLocResult mmr = Core.minMaxLoc(result);
            findMinimum.add(mmr.minVal+" "+i);
            findMinimumWithoutDegree.add(String.valueOf(mmr.minVal));
        }

        FileWriter fw = new FileWriter("findMinimum.txt");
        for(String str: findMinimum) {
            fw.write(str + System.lineSeparator());
        }
        fw.close();

        FileWriter fw2 = new FileWriter("findMinimumWithoutDegree.txt");
        for(String str: findMinimumWithoutDegree) {
            fw2.write(str + System.lineSeparator());
        }
        fw2.close();

    }
}