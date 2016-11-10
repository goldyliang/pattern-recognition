package main;

import feature.ContoursTrace;
import image.BinaryImage;
import preprocess.Smoothing;
import feature.Skeleton;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by gordon on 11/9/16.
 */
public class Assignment_3_Main {

    static int HIGHTLIGHT_BG = 0xffc0c0c0;

    static void skeletonImage (String filePath) throws IOException {
        String fileNameWOExt = filePath.substring(0, filePath.lastIndexOf("."));

        BinaryImage image = BinaryImage.loadImageFromPic (filePath);

        BinaryImage image_smoothed = image.cloneImage();
        Smoothing.smoothImage(image_smoothed);

        BinaryImage image_skeleton = image_smoothed.cloneImage();

        Skeleton.skeletonize(image_skeleton);

        image_skeleton.saveToBmp(fileNameWOExt + "-smoothed-skeleton.bmp");

        BufferedImage imgMixed = BinaryImage.getMixedImage(image_skeleton, BinaryImage.RGB_BLACK, image_smoothed, HIGHTLIGHT_BG);

        ImageIO.write (imgMixed, "BMP", new File(fileNameWOExt + "-smoothed-skeleton-highlighted.bmp"));

        image_skeleton = image.cloneImage();
        Skeleton.skeletonize(image_skeleton);
        image_skeleton.saveToBmp(fileNameWOExt + "-nonsmoothed-skeleton.bmp");

        imgMixed = BinaryImage.getMixedImage(image_skeleton, BinaryImage.RGB_BLACK, image, HIGHTLIGHT_BG);

        ImageIO.write (imgMixed, "BMP", new File(fileNameWOExt + "-nonsmoothed-skeleton-highglighted.bmp"));
    }

    public static void main (String[] args) throws IOException {

        // Do and mark skeleton for the binary image of newspaper
        skeletonImage("images/newspaper-bw.bmp");


        // Generate a color-diff image from the image of fruit
        BufferedImage img = ImageIO.read (new File("images/fruits.bmp"));
        BufferedImage img_out = BinaryImage.colorDiffImg(img, 0);
        ImageIO.write (img_out, "BMP", new File("images/fruits-colordiff-stderr.bmp"));
        img_out = BinaryImage.colorDiffImg(img, 1);
        ImageIO.write (img_out, "BMP", new File("images/fruits-colordiff-minmax.bmp"));

        // Do and mark skeleton for the binary image of fruits got from the colordiff image
        skeletonImage("images/fruits-colordiff-minmax-bw-manual.bmp");

        // Do a contour tracing for the binary image of fruits.
        BinaryImage image = BinaryImage.loadImageFromPic("images/fruits-colordiff-minmax-bw-manual.bmp");
        Smoothing.smoothImage(image);
        Collection<ContoursTrace.Contours> contours = ContoursTrace.traceAllContours_1(image, false);
        BinaryImage bwimg_out = ContoursTrace.contoursHighlightImg(image, contours);
        bwimg_out.saveToBmp("images/fruits-colordiff-minmax-bw-contours.bmp");

    }
}
