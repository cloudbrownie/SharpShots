public class Vector extends Point {

    // canon basis
    public static final Vector X_AXIS = new Vector(1, 0);
    public static final Vector Y_AXIS = new Vector(0, 1);

    // tolerance for differences, needed due to using floating point
    private static final double TOLERANCE = 1.0e-7;

    // angle for the little arrow head
    private static final double HEAD_ANGLE = Math.PI / 6; // 15 degrees

    // head length
    private static double headSizeLength = 1;

    // origin coordinates of the vector
    private double origX = 0;
    private double origY = 0;

    // default constructor
    public Vector() {
        x = 0;
        y = 0;
        origX = 0;
        origY = 0;
    }

    // constructor with components
    public Vector(double xComponent, double yComponent) {
        x = xComponent;
        y = yComponent;
    }

    // constructor with two points as x and y, points from 1 to 2
    public Vector(double x1, double y1, double x2, double y2) {
        x = x2 - x1;
        y = y2 - y1;
        origX = x1;
        origY = y1;
    }

    // constructor with two points
    public Vector(Point p1, Point p2) {
        x = p2.x - p1.x;
        y = p2.y - p1.y;
        origX = p1.x;
        origY = p1.y;
    }

    // constructor with a vector
    public Vector(Vector v) {
        x = v.x;
        y = v.y;
        origX = v.origX;
        origY = v.origY;
    }

    // constructor with a length and angle
    // set the origin of the vector
    public void setOrigin(double x, double y) {
        origX = x;
        origY = y;
    }

    // set the origin of the vector using a point
    public void setOrigin(Point p) {
        origX = p.x;
        origY = p.y;
    }

    // return the origin as a point
    public Point getOrigin() {
        return new Point(origX, origY);
    }

    // resize the head side length
    public void resizeHeadSize(double length) {
        headSizeLength = length;
    }

    // draws just the line
    public void draw(Vector scroll) {
        Point end = new Point(origX + x - scroll.x, origY + y - scroll.y);
        StdDraw.line(origX - scroll.x, origY - scroll.y, end.x, end.y);
    }

    // draws the vector with the head
    public void drawDebug(Vector scroll) {
        Point end = new Point(origX + x - scroll.x, origY + y - scroll.y);
        StdDraw.line(origX - scroll.x, origY - scroll.y, end.x, end.y);

        // draw the little arrow head to make it look cool
        if (norm() > 0) {
            double[] xHead = {
                    end.x,
                    end.x - headSizeLength,
                    end.x - headSizeLength
            };
            double[] yHead = {
                    end.y,
                    end.y - headSizeLength,
                    end.y + headSizeLength
            };

            double angle = angle(X_AXIS);
            if (angle(Y_AXIS) > Math.PI / 2) {
                angle *= -1;
            }

            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            for (int i = 0; i < xHead.length; i++) {
                double px = xHead[i] - end.x;
                double py = yHead[i] - end.y;
                xHead[i] = px * cos - py * sin + end.x;
                yHead[i] = py * cos + px * sin + end.y;
            }

            StdDraw.filledPolygon(xHead, yHead);
        }
    }

    // return the length of the edge
    public double norm() {
        return Math.sqrt((x * x) + (y * y));
    }

    // make this vector a unit vector
    public void normalize() {
        double len = norm();
        x /= len;
        y /= len;
    }

    // clamp the vector norm to value
    public void clamp(double value) {
        double length = norm();
        if (length > value) {
            x /= length / value;
            y /= length / value;
        }
    }

    // modify values of the vector to make norm equal to arg
    public void resize(double size) {
        double length = norm();
        x /= length / size;
        y /= length / size;
    }

    // make this vector a zero vector
    public void makeZero() {
        x = 0;
        y = 0;
        origX = 0;
        origY = 0;
    }

    // check if vector is zero vector
    public boolean isZero() {
        return isNormEqualTo(0);
    }

    // check if vector has non zero norm
    public boolean isNonZero() {
        return norm() > TOLERANCE;
    }

    // invert the vector
    public void invert() {
        scale(-1);
    }

    // copy values from another vector
    public void copy(Vector that) {
        x = that.x;
        y = that.y;
        origX = that.x;
        origY = that.y;
    }

    // return the dot product of this edge with a point or edge
    public double dot(Point p) {
        return x * p.x + y * p.y;
    }

    // return a vector that is perpendicular to this vector
    public Vector getPerpendicular() {
        Vector v = new Vector(-y, x);
        v.setOrigin(origX, origY);
        return v;
    }

    // return the correlation between this vector and that vector
    public double correlation(Vector v) {
        return dot(v) / (norm() * v.norm());
    }

    // return the angle, in radians, between this vector and that vector
    public double angle(Vector v) {
        return Math.acos(correlation(v));
    }

    // return the angle, in degrees, between this vector and that vector
    public double angleDeg(Vector v) {
        return Math.toDegrees(Math.acos(correlation(v)));
    }

    // return the angle of the vector, in radians
    public double angle() {
        return Math.atan2(y, x);
    }

    // return the angle of the vector, in degrees
    public double angleDeg() {
        return Math.toDegrees(angle());
    }

    // set the angle of the vector
    public void setAngle(double degrees) {
        double length = norm();

        x = Math.cos(Math.toRadians(degrees)) * length;
        y = Math.sin(Math.toRadians(degrees)) * length;
    }

    // arithmetic methods ------------------------------------------------------

    // add method using just a value
    public void add(double value) {
        double angle = angle();
        x += Math.cos(angle) * value;
        y += Math.sin(angle) * value;
    }

    // add method using a value and an angle
    public void add(double value, double degrees) {
        double angle = Math.toRadians(degrees);
        x += Math.cos(angle) * value;
        y += Math.sin(angle) * value;
    }

    // static add method
    public static Vector add(Vector v, Vector u) {
        return new Vector(v.x + u.x, v.y + u.y);
    }

    // static subtract method
    public static Vector subtract(Vector v, Vector u) {
        return new Vector(v.x - u.x, v.y - u.y);
    }

    // scale method
    public void scale(double alpha) {
        x *= alpha;
        y *= alpha;
    }

    // static scale method
    public static Vector scale(Vector v, double alpha) {
        return new Vector(v.x * alpha, v.y * alpha);
    }

    // boolean methods ---------------------------------------------------------

    // equals method, checks direction and magnitude
    public boolean equals(Vector v) {
        return x == v.x && y == v.y;
    }

    // parallel method
    public boolean isParallel(Vector v) {
        return Math.abs(correlation(v) - 1) < TOLERANCE;
    }

    // perpendicular method
    public boolean isPerpendicular(Vector v) {
        return Math.abs(correlation(v)) < TOLERANCE;
    }

    // compareTo method, used to sort by norms
    public int compareTo(Vector v) {
        if (norm() > v.norm()) {
            return 1;
        }
        else if (norm() < v.norm()) {
            return -1;
        }
        return 0;
    }

    // compare norm to a value
    public boolean isNormEqualTo(double value) {
        return Math.abs(norm() - value) < TOLERANCE;
    }

    // extra methods -----------------------------------------------------------

    public String toString() {
        return String.format("v = (%.2f, %.2f), orig = (%.2f, %.2f)",
                             x, y, origX, origY);
    }

    public static void main(String[] args) {
        // test vectors
        Vector a = new Vector(1, 0);
        Vector b = new Vector(0, 1);
        Vector c = new Vector(1, 1);
        Vector d = new Vector(1, 1);

        StdOut.println(a.angle(b));
        StdOut.println(a.angle(c));
        StdOut.println(a.isPerpendicular(b));
        StdOut.println(c.isParallel(d));
        StdOut.println(c.compareTo(a));

        Vector v = new Vector(50, 50, 60, 70);
        Vector u = new Vector(50, 50, 70, 60);
        Vector w = new Vector(50, 50, 35, 35);
        Vector x = new Vector(50, 50, 60, 35);
        Vector y = new Vector(50, 50, 35, 60);
        Vector z = new Vector(50, 50, 60, 40);

        StdDraw.setScale(0, 100);

        Vector testV = y;

        Vector scroll = new Vector();

        testV.drawDebug(scroll);

        StdOut.println(testV.angle());
        StdOut.println(testV.norm());
        testV.clamp(20);
        StdOut.println(testV.norm());
        testV.resize(20);
        StdOut.println(testV.norm());
        testV.add(5);
        StdOut.println(testV.norm());
        testV.setAngle(45);

        testV.drawDebug(scroll);
    }
}
