package binpreprocess;


public class Smoothing {
 
	private static class Mask {
		int [] [] mask;
		
		Mask (String pattern) {
			mask = new int [3][3];
			for (int i = 0; i< pattern.length(); i++) {
				mask [ i / 3] [i % 3] = pattern.charAt(i) == '0' ? 0 : 1;
			}
		}
	}
	
	private static Mask[] masks = { 
			new Mask ("110100110"),
			new Mask ("000101111"),			
			new Mask ("111101000"),
			new Mask ("011001011")
	};
	
	protected static boolean smoothPixel (BinaryImage image, int x, int y) {

		// Go through each mask
		for (int mid = 0; mid < masks.length; mid++) {
			
			// The result of smoothing (-1 if not set yet)
			int result = -1;
			
			boolean needchange = true;
			
			// For every 3x3 mask value
			for (int i=-1; i<=1; i++) {
				for (int j=-1; j<=1; j++) {
					if ( masks[mid].mask[i+1][j+1] > 0 ) {
						// Get the pixel of the adjacent, treat as background (white) if out-of-bound
						int p = image.validCord(x+i,y+j) ? image.pixel (x+i,y+j):0;

						// if any surrounding is the same as target, ignore this mask
						if (p == image.pixel (x,y))
							needchange = false;
						else {
							if (result < 0)
								// set the first result
								result = p;
							else if (result != p ) {
								// if any surrounding is different, ignore
								needchange = false;
								break;
							}
						}
					}
				}
				if (! needchange) break;
			}
			
			if (needchange) {
				image.setPixel (x,y, result);
				return true;
			}
		}
		
		return false;
	}
	
	 private static void pressAnyKeyToContinue()
	 { 
	        System.out.println("Press any key to continue...");
	        try
	        {
	        	System.in.read();
	        }  
	        catch(Exception e)
	        {}  
	 }

	public static void smoothImage(BinaryImage image, boolean interactive) {
		boolean changed = true;
		int roundn = 1;

		while ( changed ) {
			changed = false;
			for (int x = 0; x < image.width(); x++)
				for (int y = 0; y < image.height(); y++)
					if (smoothPixel(image, x, y))
						changed = true;

			if (interactive) {

				System.out.println("Round " + (roundn++));
				image.display();
				pressAnyKeyToContinue();
			}
		}
	}
	 
	public static void smoothImage(BinaryImage image) {
		smoothImage(image, false);
	}
}
