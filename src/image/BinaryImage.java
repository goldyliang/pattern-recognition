package image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.awt.image.*;
import javax.imageio.*;


public class BinaryImage {
	public int [][] pixels;

	public static class Coordinate {
		public int x;
		public int y;

		public Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public Coordinate(Coordinate cord) {
			this.x = cord.x;
			this.y = cord.y;
		}

		@Override
		public boolean equals (Object obj) {
			return (obj instanceof Coordinate &&
					((Coordinate)obj).x == x &&
					((Coordinate)obj).y == y);
		}

		@Override
		public int hashCode () {
			return Integer.hashCode(x * 65536 + y);
		}

	}

	public BinaryImage (int w, int h) {
		pixels = new int[w][h];
	}

	public int height() {
		return pixels[0].length;
	}
	
	public int width() {
		return pixels.length;
	}

	public int pixel (int x, int y) {
		return pixels [x][y];
	}

	public int pixel (Coordinate cord) {
		return pixels [cord.x][cord.y];
	}

	public void setPixel (int x, int y, int v) {
		pixels[x][y]= v;
	}

	public int pixelB (int x, int y) {
		if (validCord (x,y))
			return pixels[x][y];
		else
			return 0;
	}

	public boolean validCord (Coordinate cord) {
		return validCord (cord.x, cord.y);
	}

	public boolean validCord (int x, int y) {
		 return x >=0 && x < width() && y >=0 && y < height();
	}
	
	public static BinaryImage loadImage (String path) throws FileNotFoundException, IOException {
		
	    BinaryImage image;

		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
		    String line = br.readLine();
		    
		    int w = Integer.valueOf(line.split(",")[0]);
		    int h = Integer.valueOf(line.split(",")[1]);

			image = new BinaryImage(w,h);

		    int y = 0;
		    while ( (line = br.readLine()) != null) {
		        for ( int x = 0; x < line.length() && x < w; x++ ) {
		        	if (line.charAt(x) == ' ' || line.charAt(x) == '0')
		        		image.pixels [x][y] = 0;
					else
						image.pixels [x][y] = 1;
		        }
		        y++;
		    }
		}
		
		return image;
	}

	public static BinaryImage loadImageFromPic (String path) throws FileNotFoundException, IOException {

		BufferedImage img;

		img = ImageIO.read(new File(path));
		int w = img.getWidth();
		int h =img.getHeight();
		BinaryImage image = new BinaryImage( w, h );

		for (int x = 0; x < w; x++)
			for (int y = 0 ; y < h; y++)
			{
				int rgb = img.getRGB(x, y) & 0x00ffffff;
				image.setPixel (x,y, rgb == 0 ? 0:1);
			}

		return image;
	}

	public void saveToBmp(String path) throws IOException {
		BufferedImage img = new BufferedImage (width(), height(), BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < width(); x++)
			for (int y = 0; y < height(); y++) {
				if (pixels[x][y] > 0)
					img.setRGB(x, y, 0xffffffff);
				else
					img.setRGB(x, y, 0xff000000);
			}

		ImageIO.write (img, "BMP", new File(path));
	}
	
	public void display () {
		for (int i = 0; i < width() + 2; i++)
			System.out.print ('-');
		System.out.println();

		for (int y = 0; y < height(); y++) {
			System.out.print('|');
			for (int x = 0 ; x < width(); x++)
				System.out.print( pixels[x][y] == 0 ? ' ' : '#');
			System.out.println('|');
		}

		for (int i = 0; i < width() + 2; i++)
			System.out.print ('-');
		System.out.println();
	}

	public static BufferedImage getMixedImage (BinaryImage imgf, int rgbf, BinaryImage imgb, int rgbb) {
		BufferedImage img_r = new BufferedImage(imgf.width(), imgf.height(), BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < imgf.width(); x++)
			for (int y=0; y < imgf.height(); y++)
				if (imgf.pixels[x][y] > 0)
					img_r.setRGB(x, y, rgbf);
				else if (imgb.pixels[x][y] > 0 )
					img_r.setRGB(x, y, rgbb);
				else
					img_r.setRGB(x, y, 0xffffffff);

		return img_r;
	}

/*	public BufferedImage getRenderableImage() {
		BufferedImage img_r = new BufferedImage(width(), height(), TYPE_INT_RGB);

		for (int x = 0; x < width(); x++)
			for (int y=0; y<height(); y++)
				if (pixels[x][y] > 0)
					img.setRGB(x, y, 0xffffffff);
				else
					img.setRGB(x, y, 0xff000000);	} */
}
