package com.example.tachographanalysis.analogueAnalysis;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.io.IOException;


public class blackAndWhite {

    public static String loadImage(Image image) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String file = image.getUrl();
        Mat imageFile = Imgcodecs.imread(file
                .replace("file:/","")
        );
        return null;
    }

//    public WritableImage loadAndConvert(Image image) {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        Mat dst = new Mat();
//        String file = image.getUrl();
//        Mat imageFile = Imgcodecs.imread(file
//                .replace("file:/","")
//        );
//        Imgproc.threshold(imageFile, dst, 220, 520, Imgproc.THRESH_BINARY);
//        byte[] data1 = new byte[dst.rows() * dst.cols() * (int)(dst.elemSize())];
//        dst.get(0,0,data1);
//        BufferedImage bufferedImage = new BufferedImage(dst.cols(), dst.rows(), BufferedImage.TYPE_BYTE_BINARY);
//        bufferedImage.getRaster().setDataElements(0,0, dst.cols(), dst.rows(), data1);
//        WritableImage writableImage = SwingFXUtils.toFXImage(bufferedImage, null);
//        System.out.println("Converted to binary");
//        return writableImage;
//    }

    public WritableImage loadAndConvert(Image image) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String file = image.getUrl();
        Mat imageFile = Imgcodecs.imread(file
                .replace("file:/",""),
                Imgcodecs.IMREAD_GRAYSCALE
        );
        Mat dst = new Mat(imageFile.rows(), imageFile.cols(), imageFile.type());
        Mat dst_=new Mat(imageFile.rows(), imageFile.cols(), imageFile.type());
        Mat dst2=new Mat(imageFile.rows(), imageFile.cols(), imageFile.type());
        Imgproc.warpPolar(imageFile,
                dst_,
                imageFile.size(),
                new Point(imageFile.width()/2,imageFile.height()/2),
                imageFile.width()/2,
                0);
        Core.rotate(dst_,dst2,Core.ROTATE_90_COUNTERCLOCKWISE);
        Imgcodecs.imwrite(file
                .replace("file:/","")+"test___.jpg",dst2);
//        Imgproc.adaptiveThreshold(imageFile, dst, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 15, -2);
//        Core.bitwise_not(imageFile, dst2);
        java.awt.Image img = HighGui.toBufferedImage(dst2);
        Graphics g=new Graphics(HighGui.toBufferedImage(dst2));
        g.blackAndWhite(200);

        WritableImage writableImage = SwingFXUtils.toFXImage((BufferedImage) g.im, null);
        return writableImage;
    }

    public WritableImage findCircle(Image image) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String file = image.getUrl();
        Mat imageFile = Imgcodecs.imread(file
                        .replace("file:/",""),
                Imgcodecs.IMREAD_GRAYSCALE
        );
        Mat dst = new Mat(imageFile.rows(), imageFile.cols(), imageFile.type());
        return null;
    }

    public WritableImage getHuanByCircle(Image image) {

        return null;
    }
}
