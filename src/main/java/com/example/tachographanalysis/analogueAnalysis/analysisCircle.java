package com.example.tachographanalysis.analogueAnalysis;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.json.JSONObject;
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
        changeColor kola=new changeColor(HighGui.toBufferedImage(imageFile));
//        kola.blackAndWhite(200);
//        kola.petla_po_pikselach();
        kola.greyScale();
        kola.save("png",file
                .replace("file:/","")+"black_circle.png");
        JSONObject center=HoughCirclesRun.run(file
                .replace("file:/","")+"black_circle.png");
        System.out.println(center);
        Mat dst = new Mat(imageFile.rows(), imageFile.cols(), imageFile.type());
        Mat dst2=new Mat(imageFile.rows(), imageFile.cols(), imageFile.type());
        Imgproc.warpPolar(imageFile,
                dst,
                imageFile.size(),
                new Point(center.getDouble("centerx"),center.getDouble("centery")),
                center.getDouble("radius"),
                0);
        Core.rotate(dst,dst2,Core.ROTATE_90_COUNTERCLOCKWISE);
        dst2=dst2.submat(new Range(0,(int) (dst2.height() - dst2.height() / 2.8)),new Range(0,dst2.width()));


//        Imgcodecs.imwrite(file
//                .replace("file:/","")+"test___.jpg",dst2);
        java.awt.Image img = HighGui.toBufferedImage(dst2);
        changeColor g=new changeColor(HighGui.toBufferedImage(dst2));
        g.blackAndWhite(200);
    g.petla_po_pikselach();
        Imgproc.resize(imageFile, dst, new Size(2200,3000), 5, 5, Imgproc.INTER_AREA);
//        g.save("png",file.replace("file:/","")+"nazwa.png");
//      System.out.println(file+"nazwa.png");
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
