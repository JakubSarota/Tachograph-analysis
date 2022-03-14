package com.example.tachographanalysis.analogueAnalysis;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.io.IOException;


import static org.opencv.imgcodecs.Imgcodecs.imread;


public class analysisCircle {
    public WritableImage getHuanByCircle(String file) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat imageFile = imread(file
                .replace("file:/",""),
                Imgcodecs.IMREAD_GRAYSCALE
        );
        Mat dst = new Mat(imageFile.rows(), imageFile.cols(), imageFile.type());
        Mat dst2=new Mat(imageFile.rows(), imageFile.cols(), imageFile.type());
        Imgproc.warpPolar(imageFile,
                dst,
                imageFile.size(),
                new Point(imageFile.width()/2,imageFile.height()/2),
                imageFile.width()/2,
                0);
        Core.rotate(dst,dst2,Core.ROTATE_90_COUNTERCLOCKWISE);
        dst2=dst2.submat(new Range(0,(int) (dst2.height() - dst2.height() / 2.8)),new Range(0,dst2.width()));


//        Imgcodecs.imwrite(file
//                .replace("file:/","")+"test___.jpg",dst2);
        java.awt.Image img = HighGui.toBufferedImage(dst2);
        changeColor g=new changeColor(HighGui.toBufferedImage(dst2));
        Imgproc.resize(imageFile, dst, new Size(2200,3000), 5, 5, Imgproc.INTER_AREA);
        g.blackAndWhite(200);

        WritableImage writableImage = SwingFXUtils.toFXImage((BufferedImage) g.im, null);
        return writableImage;
    }

    public WritableImage resizeImage(String url) {

        return null;
    }

    public WritableImage findCircle(Image image) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String file = image.getUrl();
        Mat imageFile = imread(file
                        .replace("file:/",""),
                        Imgcodecs.IMREAD_GRAYSCALE
        );
        Mat dst = new Mat();


        return null;
    }


}
