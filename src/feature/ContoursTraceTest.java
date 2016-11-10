package feature;

import image.BinaryImage;
import org.junit.Test;
import feature.ContoursTrace.Contours;
import feature.ContoursTrace.Direction;
import image.BinaryImage.Coordinate;

import java.io.IOException;

public class ContoursTraceTest {

	@Test
	public void testContours () {
		BinaryImage image = new BinaryImage(5,3);
		image.pixels = new int[][] { {0,0,0,1,1}, {0,1,0,0,1}, {0,0,0,1,1} };
		
		Contours contours = new Contours (image);
		contours.addBorderCord( new Coordinate(1,1));

		assert contours.bwColor == 1;
		assert contours.containsBorder(1,1);
		assert !contours.containsBorder(1,0);
	}

	@Test
	public void testDirection () {
		Direction dir = Direction.UP;

		Coordinate cord = new Coordinate(2,2);

		dir.applyDirection(cord);

		assert cord.x == 2 && cord.y == 1;

		dir = dir.turnLeft();
		assert dir == Direction.LEFT;

		dir.applyDirection(cord);

		assert cord.x == 1 && cord.y == 1;
	}

	@Test
	public void testContoursTrace() throws IOException {
		BinaryImage image = BinaryImage.loadImage("contoustest.txt");
		image.display();

		Contours contours = ContoursTrace.traceContours(image, 2, 5, Direction.UP);

		System.out.println (contours.toString());

		assert (contours.length() == 15);

		contours = ContoursTrace.traceContours(image, 1, 1, Direction.RIGHT);

		System.out.println (contours.toString());
		assert (contours.length() == 1);

		contours = ContoursTrace.traceContours(image, 0, 6, Direction.UP);

		System.out.println (contours.toString());
		assert (contours.length() == 28);
	}

	/*
	@Test
	public void testContoursTrace2() throws IOException {
		BinaryImage image = BinaryImage.loadImage("contoustest2.txt");
		//image.display();

		Collection<Contours> contourses = ContoursTrace.traceAllContours(image, null);

		image.saveToBmp("contourstest2.bmp");
		BufferedImage img = ImageIO.read(new File("contourstest2.bmp"));

		for (Contours contours:contourses) {
			System.out.println(contours.toString());
			contours.highlightInImage(img, 0xffff0000);
		}

		ImageIO.write (img, "BMP", new File("contourstest2-2.bmp"));

	} */

}
