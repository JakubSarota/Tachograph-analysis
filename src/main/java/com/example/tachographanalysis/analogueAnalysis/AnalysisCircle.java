package com.example.tachographanalysis.analogueAnalysis;

import org.json.JSONObject;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.io.IOException;


import static org.opencv.imgcodecs.Imgcodecs.imread;


public class AnalysisCircle {
    public static ChangeColor blackImage;
    public static ChangeColor blackImage2;
    public BufferedImage[] getHuanByCircle(String file) throws IOException, InterruptedException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat imageFile = imread(file
                .replace("file:/",""),
                Imgcodecs.IMREAD_GRAYSCALE
        );

//        work(imageFile);
        Mat dst = new Mat(imageFile.rows(), imageFile.cols(), imageFile.type());
        Mat dst2=new Mat(imageFile.rows(), imageFile.cols(), imageFile.type());
        Mat dstResize=new Mat(imageFile.rows(), imageFile.cols(), imageFile.type());
        ChangeColor kola=new ChangeColor(HighGui.toBufferedImage(imageFile));

//        kola.blackAndWhite(200);
//        kola.petla_po_pikselach();
        kola.greyScale();
        kola.save("png",file
                .replace("file:/","")+"black_circle.png");

        JSONObject center = HoughCirclesRun.run(file
                .replace("file:/","")+"black_circle.png");

        if(center==null){
            return new BufferedImage[]{null, null};
        }

        Mat findedCircle = imageFile;
        int minX=(int) (center.getDouble("centerx")-center.getDouble("radius"));
        int minY=(int) (center.getDouble("centery")-center.getDouble("radius"));
        int maxX= (int) (center.getDouble("centerx")+center.getDouble("radius"));
        int maxY=(int) (center.getDouble("centery")+center.getDouble("radius"));
        if(minX<0)
            minX=0;
        if(maxX>findedCircle.width())
            maxX=findedCircle.width();
        if(minY<0)
            minY=0;
        if(maxY>findedCircle.height())
            maxY=findedCircle.height();
        findedCircle=findedCircle.submat(
                new Range(minY,maxY),
                new Range(minX,maxX));

        Mat rotateImage = RotateImage.RotateImage(findedCircle);
//        rotateImage.copyTo(findedCircle);

        Imgproc.warpPolar(rotateImage, dst, rotateImage.size(),
                new Point(rotateImage.width()/2,rotateImage.height()/2),
                rotateImage.width()/2,
//                new Point(center.getDouble("centerx"),center.getDouble("centery")),
//                center.getDouble("radius"),
                0);

        Core.rotate(dst,dst2,Core.ROTATE_90_COUNTERCLOCKWISE);

        dst2=dst2.submat(new Range(0,(int) (dst2.height() - dst2.height() / 2.8)),new Range(0,dst2.width()));
        dstResize=resizeImage(dst2, dstResize, (int) ( rotateImage.width()*4));

        Mat work = CropWork.crop(dstResize);

        blackImage = new ChangeColor(HighGui.toBufferedImage(dst2));
        blackImage2 = new ChangeColor(HighGui.toBufferedImage(dstResize));

        blackImage.blackAndWhite(200);
        blackImage2.blackAndWhite(200);
//        blackImage.czas_pracy();
//        java.awt.Image img = HighGui.toBufferedImage(dstResize);

        BufferedImage writableImage = (BufferedImage) blackImage2.im;
        return new BufferedImage[]{writableImage, (BufferedImage) HighGui.toBufferedImage(rotateImage)};
    }

    private Mat resizeImage(Mat dst, Mat dstResize, int width) {
        Size Resize = new Size(width, dst.height());
        Imgproc.resize(dst, dstResize, Resize, 0,0, Imgproc.INTER_AREA);
        return dstResize;
    }

}
