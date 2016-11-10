package preprocess;

import image.BinaryImage;
import org.junit.Test;

public class SmoothingTest {

	@Test
	public void testSmoothPixel () {
		BinaryImage image = new BinaryImage(5,3);
		image.pixels = new int[][] { {0,0,0}, {0,1,0}, {0,0,0}, {1,0,1}, {1,1,1}};
		
		image.display();
		
		System.out.println();
		
		//boolean b = Smoothing.smoothPixel(image, 1, 1);
		Smoothing.smoothImage(image);
		
		image.display();
	}

}
