package feature;

import org.junit.Test;
import image.BinaryImage;

import java.io.IOException;

/**
 * Created by elnggng on 11/7/16.
 */
public class Skeleton {

    final static int N_NEIGHBOURS = 8;
    final static int[] DX = {0, 1, 1, 1, 0, -1, -1, -1};
    final static int[] DY = {-1, -1, 0, 1, 1, 1, 0, -1};

    static boolean tryRemove (BinaryImage image, BinaryImage image_dest, int x, int y, int sub_num) {

        if (image.pixel(x,y) == 0) {
            image_dest.setPixel(x,y,0);
            return false;
        }

        int Bp = 0;
        int Ap = 0;

        int last = image.pixelB(x + DX[N_NEIGHBOURS - 1], y + DY[N_NEIGHBOURS - 1]);

        int p[] = new int[9];

        boolean needChange = false;

        for (int i = 1; i <= N_NEIGHBOURS; i++) {
            int x1 = x + DX[i - 1];
            int y1 = y + DY[i - 1];

            p[i] = image.pixelB(x1, y1);

            if (p[i] == 1)
                Bp++;

            if (last == 0 && p[i] == 1)
                Ap++;

            last = p[i];
        }

        if (Bp >= 2 && Bp <= 6 && Ap == 1) {
            if (sub_num == 1) {
                if (p[1] * p[3] * p[5] == 0 && p[3] * p[5] * p[7] == 0) {
                    needChange = true;
                }
            } else {
                if (p[1] * p[3] * p[7] == 0 && p[1] * p[5] * p[7] == 0) {
                    needChange = true;
                }
            }
        }

        if (needChange)
            image_dest.setPixel(x, y, 0);
        else
            image_dest.setPixel(x, y, 1);

        return needChange;
    }

    public static boolean iterate (BinaryImage image, BinaryImage image_dest, int sub_num) {

        boolean hasChange = false;

        for (int x = 0; x < image.width(); x++)
            for (int y = 0; y < image.height(); y++) {
                if (tryRemove(image, image_dest, x, y, sub_num))
                    hasChange = true;
            }

        return hasChange;
    }

    public static void skeletonize (BinaryImage image_orig) throws IOException {

        boolean finish = false;

        int cnt = 0;

        BinaryImage image = image_orig;
        BinaryImage image_dest = image.cloneImage();

        BinaryImage tmp;
        while (!finish || image != image_orig) {

            boolean c1 = iterate(image, image_dest, 1);
            //image_dest.saveToBmp(Integer.toString(cnt) + "-1.bmp");

            tmp = image; image = image_dest; image_dest = tmp;
            boolean c2 = iterate(image, image_dest, 2);
            //image_dest.saveToBmp(Integer.toString(cnt) + "-2.bmp");

            tmp = image; image = image_dest; image_dest = tmp;

            finish = !c1 && !c2;
            cnt++;
        }

    }

    @Test
    public void testIter() {
        BinaryImage image = new BinaryImage(3,3);
        image.pixels = new int[][]
                { {0,1,1},
                        {1,1,0},
                        {1,0,1}};
        assert !tryRemove(image, image, 1, 1, 1);
        assert image.pixel(1,1) == 1;

        image.pixels = new int[][] { {1,1,1},
                {1,1,0},
                {1,0,0}};

        assert tryRemove(image, image, 1, 1, 1);
        assert image.pixel(1,1) == 0;

        image.pixels = new int[][]
                { {0,0,1},
                        {0,1,1},
                        {1,1,1}};

        assert tryRemove(image, image, 1, 1, 1);
        assert image.pixel(1,1) == 0;


        image.display();


        image.pixels = new int[][]
                { {0,1,1},
                        {1,1,0},
                        {1,0,1}};
        assert !tryRemove(image, image, 1, 1, 2);
        assert image.pixel(1,1) == 1;

        image.pixels = new int[][]
                { {0,0,1},
                        {0,1,1},
                        {1,1,1}};

        assert tryRemove(image, image, 1, 1, 2);
        assert image.pixel(1,1) == 0;

        image.pixels = new int[][]
                { {1,1,1},
                        {1,1,0},
                        {1,0,0}};

        assert tryRemove(image, image, 1, 1, 2);
        assert image.pixel(1,1) == 0;


        image.pixels = new int[][]
                { {1,1,0},
                        {1,1,0},
                        {1,1,0}};

        assert tryRemove(image, image, 1, 1, 1);
        assert image.pixel(1,1) == 0;


        image.display();
    }


}