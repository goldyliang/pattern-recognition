package preprocess;

import java.awt.image.BufferedImage;

/**
 * Created by gordon on 11/10/16.
 */
public class Binarizing {

    public static BufferedImage colorDiffImg (BufferedImage img, int algo) {
        BufferedImage img_out = new BufferedImage( img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < img.getWidth(); x++)
            for (int y= 0; y < img.getHeight(); y++) {
                int r = (img.getRGB(x, y) & 0x00ff0000) >> 16;
                int g = (img.getRGB(x, y) & 0x0000ff00) >> 8;
                int b = img.getRGB(x, y) & 0x000000ff;

                double var;

                if (algo==0) {
                    double avg = (r + g + b) / 3.0;
                    var = Math.sqrt(((r - avg) * (r - avg) + (g - avg) * (g - avg) + (b - avg) * (b - avg)) / 3);
                } else {
                    var = Math.max( Math.max(r,g), b) - Math.min (Math.min(r,g), b);
                }

                int gray = (int) (Math.round (var));

                if (gray > 255) gray = 255;

                gray = 255 - gray;

                int rgb = 0xff000000 + (gray << 16) + (gray << 8) + gray;

                img_out.setRGB(x, y, rgb);
            }

        return img_out;
    }

}
