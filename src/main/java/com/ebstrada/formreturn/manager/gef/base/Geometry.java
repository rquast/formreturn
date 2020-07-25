package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * A library of functions that do geometric opeations. These are all static
 * methods, so you never need to make an instance of this class.
 * Needs-More-Work: many of these are not done yet or not used yet.
 */

public class Geometry {

    /**
     * Given a Rectangle and a point, set res to be the point on or in the
     * Rectangle that is closest to the given point.
     */
    public static void ptClosestTo(Rectangle r, Point p, Point res) {
        int x1 = Math.min(r.x, r.x + r.width);
        int y1 = Math.min(r.y, r.y + r.height);
        int x2 = Math.max(r.x, r.x + r.width);
        int y2 = Math.max(r.y, r.y + r.height);
        int c = 0;
        if (p.x < x1) {
            c = 0;
        } else if (p.x > x2) {
            c = 2;
        } else {
            c = 1;
        }

        if (p.y < y1) {
            c += 0;
        } else if (p.y > y2) {
            c += 6;
        } else {
            c += 3;
        }

        switch (c) {
            case 0:
                res.x = x1;
                res.y = y1;
                return; // above, left
            case 1:
                res.x = p.x;
                res.y = y1;
                return; // above
            case 2:
                res.x = x2;
                res.y = y1;
                return; // above, right
            case 3:
                res.x = x1;
                res.y = p.y;
                return; // left
            case 4:
                res.x = p.x;
                res.y = p.y;
                return; // inside rect
            case 5:
                res.x = x2;
                res.y = p.y;
                return; // right
            case 6:
                res.x = x1;
                res.y = y2;
                return; // below, left
            case 7:
                res.x = p.x;
                res.y = y2;
                return; // below
            case 8:
                res.x = x2;
                res.y = y2;
                return; // below right
        }
    }

    /**
     * Given a Rectangle and a point, return a new Point on or in the Rectangle
     * that is closest to the given Point.
     */
    public static Point ptClosestTo(Rectangle r, Point p) {
        Point res = new Point(0, 0);
        ptClosestTo(r, p, res);
        return res;
    }

    /**
     * Return the angle of a line drawn from P1 to P2 as if P1 was the origin of
     * this graph
     *
     * <pre>
     *
     *            90
     *            |
     *            |
     *            |
     *            |
     * 180 -------p1------- 0
     *            |
     *            |
     *            |
     *            |
     *           270
     * </pre>
     */
    public static double segmentAngle(Point p1, Point p2) {
        if (p2.x == p1.x) {
            if (p2.y > p1.y) {
                return 90;
            } else {
                return 270;
            }
        } else if (p2.y == p1.y) {
            if (p2.x > p1.x) {
                return 0;
            } else {
                return 180;
            }
        }
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double m = dy / dx;
        double a = Math.atan(m) * 180 / Math.PI;
        if (dx < 0) {
            a = 180 + a;
        } else if (dy < 0) {
            a = 360 + a;
        }
        return a;
    }

    /**
     * Given two angle values as calculated using segmentAngle calculate the
     * angle gap between the two.
     *
     * @param angle1
     * @param angle2
     * @return the angle difference.
     */
    public static double diffAngle(double angle1, double angle2) {
        double diff = Math.abs(angle1 - angle2);
        if (diff > 180) {
            diff = 360 - diff;
        }
        return diff;
    }

    /**
     * Given the coordinates of the endpoints of a line segment, and a point,
     * set res to be the closest point on the segement to the given point.
     */
    public static void ptClosestTo(int x1, int y1, int x2, int y2, Point p, Point res) {
        // segment is a point
        if (y1 == y2 && x1 == x2) {
            res.x = x1;
            res.y = y1;
            return;
        }
        // segment is horizontal
        if (y1 == y2) {
            res.y = y1;
            res.x = mid(x1, x2, p.x);
            return;
        }
        // segment is vertical
        if (x1 == x2) {
            res.x = x1;
            res.y = mid(y1, y2, p.y);
            return;
        }
        int dx = x2 - x1;
        int dy = y2 - y1;
        res.x = dy * (dy * x1 - dx * (y1 - p.y)) + dx * dx * p.x;
        res.x = res.x / (dx * dx + dy * dy);
        res.y = (dx * (p.x - res.x)) / dy + p.y;

        if (x2 > x1) {
            if (res.x > x2) {
                res.x = x2;
                res.y = y2;
            } else if (res.x < x1) {
                res.x = x1;
                res.y = y1;
            }
        } else {
            if (res.x < x2) {
                res.x = x2;
                res.y = y2;
            } else if (res.x > x1) {
                res.x = x1;
                res.y = y1;
            }
        }
    }

    /**
     * Given three ints, return the one with the middle value. I.e., it is not
     * the single largest or the single smallest.
     */
    private static int mid(int a, int b, int c) {
        if (a <= b) {
            if (b <= c) {
                return b;
            } else if (c <= a) {
                return a;
            } else {
                return c;
            }
        }
        if (b >= c) {
            return b;
        } else if (c >= a) {
            return a;
        } else {
            return c;
        }
    }

    /**
     * Given the coordinates of the endpoints of a line segment, and a point,
     * return a new point that is the closest point on the segement to the given
     * point.
     */
    public static Point ptClosestTo(int x1, int y1, int x2, int y2, Point p) {
        Point res = new Point(0, 0);
        ptClosestTo(x1, y1, x2, y2, p, res);
        return res;
    }

    /**
     * Given the endpoints of a line segment, and a point, return a new point
     * that is the closest point on the segement to the given point.
     */
    public static Point ptClosestTo(Point p1, Point p2, Point p) {
        return ptClosestTo(p1.x, p1.y, p2.x, p2.y, p);
    }

    private static Point tempPoint = new Point(0, 0);

    /**
     * Given a polygon and a point, set res to be the point on the perimiter of
     * the polygon that is closest to to the given point.
     */
    public static synchronized void ptClosestTo(int xs[], int ys[], int n, Point p, Point res) {
        res.x = xs[0];
        res.y = ys[0];
        int bestDist = (res.x - p.x) * (res.x - p.x) + (res.y - p.y) * (res.y - p.y);
        int tDist;
        tempPoint.x = 0;
        tempPoint.y = 0;
        for (int i = 0; i < n - 1; ++i) {
            ptClosestTo(xs[i], ys[i], xs[i + 1], ys[i + 1], p, tempPoint);
            tDist = (tempPoint.x - p.x) * (tempPoint.x - p.x) + (tempPoint.y - p.y) * (tempPoint.y
                - p.y);
            if (bestDist > tDist) {
                bestDist = tDist;
                res.x = tempPoint.x;
                res.y = tempPoint.y;
            }
        }
        // dont check segment xs[n-1],ys[n-1] to xs[0],ys[0] because I assume
        // xs[n-1] == xs[0] && ys[n-1] == ys[0], if it is a closed polygon
    }

    /**
     * Given a polygon and a point, return a new point on the perimiter of the
     * polygon that is closest to to the given point.
     */
    public static Point ptClosestTo(int xs[], int ys[], int n, Point p) {
        Point res = new Point(0, 0);
        ptClosestTo(xs, ys, n, p, res);
        return res;
    }

    /**
     * Reply true iff the given point is within grip pixels of one of the
     * segments of the given polygon. Needs-more-work: this is never used, I
     * don't know that it is needed now that I use hit rectangles instead.
     */
    public static synchronized boolean nearPolySegment(int xs[], int ys[], int n, int x, int y,
        int grip) {
        for (int i = 0; i < n - 1; ++i) {
            int x1 = xs[i], y1 = ys[i];
            int x2 = xs[i + 1], y2 = ys[i + 1];
            if (Geometry.nearSegment(x1, y1, x2, y2, x, y, grip)) {
                return true;
            }
        }
        return false;
    }

    private static Rectangle tempRect1 = new Rectangle();

    /**
     * Reply true if the given point is within grip pixels of the given segment.
     * Needs-more-work: this is never used, I don't know that it is needed now
     * that I use hit rectangles instead.
     */
    public static synchronized boolean nearSegment(int x1, int y1, int x2, int y2, int x, int y,
        int grip) {
        tempRect1.setBounds(x - grip, y - grip, 2 * grip, 2 * grip);
        return intersects(tempRect1, x1, y1, x2, y2);
    }

    /**
     * Reply true if the given Rectangle intersects the given line segment.
     */
    public static synchronized boolean intersects(Rectangle r, int x1, int y1, int x2, int y2) {
        return r.intersectsLine(x1, y1, x2, y2);
    }

    /**
     * Reply true if the given point is counter-clockwise from the vector
     * defined by the position of the given line. This is used as in determining
     * intersection between lines and rectangles. Taken from Algorithms in C by
     * Sedgewick, page 350.
     */
    public static int counterClockWise(int x1, int y1, int x2, int y2, int x, int y) {
        int dx1 = x2 - x1;
        int dy1 = y2 - y1;
        int dx2 = x - x1;
        int dy2 = y - y1;
        if (dx1 * dy2 > dy1 * dx2) {
            return +1;
        }
        if (dx1 * dy2 < dy1 * dx2) {
            return -1;
        }
        if ((dx1 * dx2 < 0) || (dy1 * dy2 < 0)) {
            return -1;
        }
        if ((dx1 * dx1 + dy1 * dy1) < (dx2 * dx2 + dy2 * dy2)) {
            return +1;
        }
        return 0;
    }
} /* end class Geometry */
