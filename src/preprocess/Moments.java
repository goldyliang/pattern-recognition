package preprocess;

import image.BinaryImage;

/**
 * Created by gordon on 10/9/16.
 */
public class Moments {

    static double TIMES_SIZE_NORM  = 5;

    private static int iPwr (int v, int n) {
        int r = 1;

        while (n--  > 0) r = r * v;

        return r;
    }

    private static int round (double v) { return (int) Math.round(v);}

    static double GeoMoment (BinaryImage img, int p, int q) {
        return CentralMoment (img, 0, 0, p, q);
    }

    static double CentralMoment (BinaryImage img, int xc, int yc, int p, int q) {
        double m = 0;
        for (int x = 0; x < img.width(); x++)
            for (int y = 0; y < img.height(); y++)
                if (img.pixel (x,y) > 0) {
                    m += iPwr( x - xc , p) * iPwr( y - yc, q) * img.pixel (x,y);
                }
        return m;
    }

    public static BinaryImage normalized (BinaryImage img, int L, ImgStatistic ch) {

        double weight = GeoMoment(img, 0 , 0);
        int xc = round (GeoMoment (img, 1, 0) / weight);
        int yc = round (GeoMoment (img, 0, 1) / weight);

        int H1 = round (TIMES_SIZE_NORM * Math.sqrt( CentralMoment(img, xc, yc, 0, 2) / weight));
        int W1 = round (TIMES_SIZE_NORM * Math.sqrt( CentralMoment(img, xc, yc, 2, 0) / weight));

        double R1 = (H1 < W1 ? (double)H1 / W1 : (double)W1 / H1);

        double R2 = 1;

        int mHW2 = round(R2 * L);

        int H2 = (H1 < W1 ? mHW2: L);
        int W2 = (H1 < W1 ? L : mHW2);

        double alpha = (double) W2 / W1;
        double beta = (double) H2 / H1;

        BinaryImage n_img = new BinaryImage(L, L);

        double xcp = (L-1) / 2.0;
        double ycp = (L-1) / 2.0;

        for (int xp = 0; xp < L; xp++)
            for (int yp = 0; yp < L; yp++) {
                int x = round ( (xp - xcp) / beta + xc );
                int y = round ( (yp - ycp) / alpha + yc );
                n_img.setPixel (xp, yp, img.pixelB(x,y));
            }

        ch.xc = xc;
        ch.yc = yc;
        ch.deltax = W1;
        ch.deltay = H1;

        return n_img;
    }
}
