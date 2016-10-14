package binpreprocess;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import binpreprocess.ContoursTrace.Contours;
import binpreprocess.BinaryImage.Coordinate;

public class TestMain {

	public static void main1 (String path) throws FileNotFoundException, IOException  {
		BinaryImage image = //BinaryImage.loadImageFromPic("stamp2.gif");
				BinaryImage.loadImage(path);

		image.display();

		Smoothing.smoothImage(image);

		image.display();

		ImgStatistic ch = new ImgStatistic();

		BinaryImage n_img = Moments.normalized(image, 16, ch);

		n_img.display();

		System.out.format("xc=%d, yc=%d, delta_x=%f.2, delta_y=%f.2\n", ch.xc, ch.yc, ch.deltax, ch.deltay );

		//image.saveToBmp("stamp2-1.gif");
	}

	public static void traceAndMark (BinaryImage image, int x0, int y0, int x1, int y1, int color, int level, BufferedImage image_buf, boolean recursive, boolean print) {
		Contours border = Contours.squarContours(
				image, new Coordinate(x0,y0), new Coordinate(x1,y1), color);
		border.markInBinImage(image, color);

		ContoursTrace.RecursiveContours contourses = ContoursTrace.traceAllContours(image, border, level, recursive, print);

		contourses.highlightInImage(image_buf, 0xffff0000);
	}

	public static void main2 () throws FileNotFoundException, IOException  {
		//BinaryImage image = BinaryImage.loadImage("contoustest2.txt");
		BinaryImage image = BinaryImage.loadImageFromPic("stamp2.gif");
		//image.display();

		image.saveToBmp("stamp2-2.bmp");
		BufferedImage img = ImageIO.read(new File("stamp2-2.bmp"));

		traceAndMark(image, 42,46,1360,1002, 1, 1, img, false, true);
		traceAndMark(image, 142,808, 400,926, 0, 1, img, false, true);

		ImageIO.write (img, "BMP", new File("stamp2-2.bmp"));
	}

	public static void main3 (String bwPath, String origPath, int level, boolean recursive, boolean print) throws FileNotFoundException, IOException  {
		//BinaryImage image = BinaryImage.loadImage("contoustest2.txt");
		BinaryImage image = BinaryImage.loadImageFromPic(bwPath);

		//Smoothing.smoothImage(image);

		String [] paths = bwPath.split("\\.");
		String outFile = paths[0] + "-smoothed.bmp";
		image.saveToBmp(outFile);

		//BufferedImage img = ImageIO.read(new File(origPath));
		BufferedImage img = ImageIO.read(new File(outFile));

		traceAndMark(image, 0, 0, image.width()-1, image.height()-1, 1, level, img, recursive, print);
		//traceAndMark(image, 142,808, 400,926, 0, img);

		outFile = paths[0] + "-contours.bmp";
		ImageIO.write (img, "BMP", new File(outFile));
	}

	public static void main (String[] args) throws FileNotFoundException, IOException {
		
		//main3 ("coin-bw.bmp");
//		main3 ("stamp2.bmp", "stamp2.bmp", 3, true, false);

		//main3 ("coin-bw.bmp", "coin-bw.bmp", 2, true, false);

		main1("test1.txt");
	}
}
