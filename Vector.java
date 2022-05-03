public class Vector extends Point {

    // static vars -------------------------------------------------------------

    // canon basis
    public static final Vector X_AXIS = new Vector(1, 0);
    public static final Vector Y_AXIS = new Vector(0, 1);

    // static methods ----------------------------------------------------------

    // static add method
    public static Vector add(Vector v, Vector u) {
        Vector a = new Vector(v.x + u.x, v.y + u.y);
        a.setOrigin(v.getOrigin());
        return a;
    }

    // static scale method
    public static Vector scale(Vector v, double alpha) {
        Vector a = new Vector(v.x * alpha, v.y * alpha);
        a.setOrigin(v.getOrigin());
        return a;
    }

    // generate random vector with random angle
    public static Vector genVector(double mag, double degrees) {
        Vector v = new Vector(mag, 0);
        v.rotate(degrees);
        return v;
    }

    // instance vars -----------------------------------------------------------

    // origin coordinates of the vector
    protected double origX;
    protected double origY;

    // constructors ------------------------------------------------------------

    // default constructor
    public Vector() {
        x = 0;
        y = 0;
        origX = 0;
        origY = 0;
    }

    // constructor with components
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
        origX = 0;
        origY = 0;
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

    // getters -----------------------------------------------------------------

    // return the origin as a point
    public Point getOrigin() {
        return new Point(origX, origY);
    }

    // return a vector that is perpendicular to this vector
    public Vector getPerpendicular() {
        Vector v = new Vector(-y, x);
        v.setOrigin(origX, origY);
        return v;
    }

    // setters -----------------------------------------------------------------

    // constructor with a length and angle
    public void setOrigin(double x, double y) {
        origX = x;
        origY = y;
    }

    // set the origin of the vector using a point
    public void setOrigin(Point p) {
        origX = p.x;
        origY = p.y;
    }

    // other methods -----------------------------------------------------------

    // check if vector has non zero norm
    public boolean isNonZero() {
        return norm() > Constants.TOLERANCE;
    }

    // return the length of the edge
    public double norm() {
        return Math.sqrt((x * x) + (y * y));
    }

    // return the dot product of this edge with a point or edge
    public double dot(Point p) {
        return x * p.x + y * p.y;
    }

    // return the correlation between this vector and that vector
    public double correlation(Vector v) {
        return dot(v) / (norm() * v.norm());
    }

    // return angle between vectors
    public double angle(Vector v) {
        return Math.acos(correlation(v));
    }

    // return the angle of the vector, in radians
    public double angle() {
        return Math.atan2(y, x);
    }

    // return the angle of the vector, in degrees
    public double angleDeg() {
        return Math.toDegrees(angle());
    }

    // return the rotational angle of the vector, in radians
    public double rotAngle() {
        double angle = angle();
        if (angle < 0) {
            angle = 2 * Math.PI + angle;
        }
        return angle;
    }

    // return the rotational angle of the vector, in degrees
    public double rotAngleDeg() {
        return Math.toDegrees(rotAngle());
    }

    public String toString() {
        return String.format("v = (%.2f, %.2f), orig = (%.2f, %.2f)",
                             x, y, origX, origY);
    }

    // draws the vector with the head
    public void drawDebug(Vector scroll) {
        Point end = new Point(origX + x - scroll.x, origY + y - scroll.y);
        StdDraw.line(origX - scroll.x, origY - scroll.y, end.x, end.y);

        // draw the little arrow head to make it look cool
        if (norm() > 0) {
            // head length
            double headSizeLength = 1;
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

    // make this vector a unit vector
    public void normalize() {
        double len = norm();
        x = x / len;
        y = y / len;
    }

    // clamp the vector norm to value
    public void clamp(double value) {
        double length = norm();
        if (length > value) {
            x = x / (length / value);
            y = y / (length / value);
        }
    }

    // modify values of the vector to make norm equal to arg
    public void resize(double size) {
        double length = norm();
        x = x / (length / size);
        y = y / (length / size);
    }

    // invert the vector
    public void invert() {
        x *= -1;
        y *= -1;
    }

    // copy values from another vector
    public void copy(Vector that) {
        x = that.x;
        y = that.y;
        origX = that.x;
        origY = that.y;
    }

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

    // subtract method usinga value
    public void subtract(double value) {
        double angle = angle();
        x -= Math.cos(angle) * value;
        y -= Math.sin(angle) * value;
    }

    // scale method
    public void scale(double alpha) {
        x *= alpha;
        y *= alpha;
    }

    public void rotate(double degrees) {
        double angle = rotAngleDeg() + degrees;
        double d = norm();
        x = d * Math.cos(Math.toRadians(angle));
        y = d * Math.sin(Math.toRadians(angle));
    }

    public static void main(String[] args) {
    }
}
