package binpreprocess;

import java.awt.image.BufferedImage;
import java.util.*;

import binpreprocess.BinaryImage.Coordinate;

/**
 * Created by gordon on 10/12/16.
 */
public class ContoursTrace {


    public static class Contours {

        public BinaryImage image;

        int bwColor = -1;

        LinkedHashSet <Coordinate> coordinates = new LinkedHashSet<>();

        public Contours() {

        }

        public Contours (BinaryImage image) {
            this.image = image;
        }

        public void addBorderCord(Coordinate cord) {
            coordinates.add ( new Coordinate(cord) ) ;
            if (bwColor < 0)
                bwColor = image.pixel (cord);
            //else if (bwColor != image.pixel(cord))
            //    throw new RuntimeException("Original contours color not match the new border pixel");

        }

        public boolean containsBorder(int x, int y) {
            return coordinates.contains ( new Coordinate(x,y));
        }

        public boolean containsBorder(Coordinate cord) {
            return coordinates.contains ( cord);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            Iterator<Coordinate> cordIter = coordinates.iterator();

            while (cordIter.hasNext()) {
                Coordinate cord = cordIter.next();
                builder.append("(")
                        .append (cord.x)
                        .append (",")
                        .append (cord.y)
                        .append (")");
            }
            return builder.toString();
        }

        public int length() {
            return coordinates.size();
        }

        public void highlightInImage (BufferedImage img, int rgb) {
            Iterator<Coordinate> cordIter = coordinates.iterator();

            while (cordIter.hasNext()) {
                Coordinate cord = cordIter.next();
                img.setRGB(cord.x, cord.y, rgb);
            }
        }

        public Coordinate getFarEndBorder(Coordinate cord, Direction dir) {
            Coordinate cur = new Coordinate(cord);
            Coordinate end = null;

            while (image.validCord(cur)){
                if (containsBorder(cur))
                    end = new Coordinate(cur);
                dir.applyDirection(cur);
            }
            return end;
        }

        public Coordinate getNextBorder (Coordinate cord, Direction dir) {
            Coordinate cur = new Coordinate(cord);

            while (image.validCord(cur)){
                if (containsBorder(cur))
                    return cur;
                dir.applyDirection(cur);
            }
            return null;
        }

        public static Contours squarContours (BinaryImage image, Coordinate a, Coordinate b, int color) {
            Contours border = new Contours(image);
            for (int y = a.y; y<=b.y ; y++)
                border.addBorderCord(new Coordinate(a.x, y));
            for (int x = a.x; x<=b.x ; x++)
                border.addBorderCord(new Coordinate(x, b.y));
            for (int y = b.y; y>=a.y; y--)
                border.addBorderCord(new Coordinate(b.x, y));
            for (int x = b.x; x>=a.x; x--)
                border.addBorderCord(new Coordinate(x, a.y));

            border.bwColor = color;
            return border;
        }

        public void markInBinImage (BinaryImage image, int color) {
            for (Coordinate cord : this.coordinates) {
                image.setPixel(cord.x, cord.y, color);
            }
        }

    }

    public static class RecursiveContours extends Contours {

        public Collection<RecursiveContours> subContours = new ArrayList<>();

        public RecursiveContours (Contours border) {
            image = border.image;
            bwColor = border.bwColor;
            coordinates = border.coordinates;
        }
        public RecursiveContours(BinaryImage image) {
            super (image);
        }

        @Override
        public void highlightInImage (BufferedImage img, int rgb) {
            super.highlightInImage(img, rgb);

            for (RecursiveContours c : this.subContours) {
                c.highlightInImage(img, rgb);
            }
        }
    }

    public static enum Direction {

        UP (0, -1),
        DOWN (0, 1),
        LEFT (-1, 0),
        RIGHT (1, 0);

        private final int dx, dy;

        Direction (int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        public Direction turnLeft () {
            switch (this) {
                case UP: return LEFT;
                case DOWN: return RIGHT;
                case LEFT: return DOWN;
                case RIGHT: return UP;
                default: throw new RuntimeException("Invalid direction");
            }
        }

        public Direction turnRight () {
            switch (this) {
                case UP: return RIGHT;
                case DOWN: return LEFT;
                case LEFT: return UP;
                case RIGHT: return DOWN;
                default: throw new RuntimeException("Invalid direction");
            }
        }

        public void applyDirection (Coordinate cord) {
            cord.x += dx;
            cord.y += dy;
        }
    }

    public static Contours traceContours (BinaryImage image, int startX, int startY, Direction startDir) {
        Contours contours = new Contours(image);

        int color = image.pixel (startX, startY);

        Coordinate cordStart = new Coordinate(startX, startY);
        Coordinate cordCur = new Coordinate(startX, startY);
        Direction dirCur = startDir;

        do {
            if (image.validCord(cordCur) && image.pixel (cordCur) == color) {
                contours.addBorderCord(cordCur);
                dirCur = dirCur.turnLeft();
            } else
                dirCur = dirCur.turnRight();

            dirCur.applyDirection(cordCur);
        } while (! cordCur.equals(cordStart));

        return contours;
    }

    public static RecursiveContours traceAllContours (BinaryImage image, Contours border, int maxLevel, boolean recursive, boolean print) {
        Collection<RecursiveContours> contourses = new ArrayList<> ();

        for (int y = image.height()-1; y >=0; y--) {

            Coordinate start = border.getNextBorder( new Coordinate(0,y), Direction.RIGHT );
            if (start!=null) {
                Coordinate end = border.getFarEndBorder(start, Direction.RIGHT);

                int x = start.x + 1;

                while (x < end.x) {
                    Contours inFoundContous = null;

                    for (Contours c : contourses)
                        if (c.containsBorder(x,y)) {
                            inFoundContous = c;
                            break;
                        }
                    if (inFoundContous != null)
                        x = inFoundContous.getFarEndBorder(new Coordinate(x,y), Direction.RIGHT).x;
                    else {
                        if (image.pixel(x,y) != border.bwColor) {
                            // we found a start point
                            Contours newContours = traceContours(image, x, y, Direction.UP);

                            if (print) {
                                System.out.println (newContours.toString());
                            }

                            RecursiveContours recurContours = new RecursiveContours(newContours);

                            if (recursive && maxLevel > 1) {
                                recurContours.subContours = traceAllContours(image, newContours, maxLevel - 1, recursive, print).subContours;
                            }
                            contourses.add(recurContours);
                        }
                    }
                    x++;
                }
            }
        }

        RecursiveContours r = new RecursiveContours(border);
        r.subContours = contourses;

        return r;
    }

}
