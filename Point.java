public class Point {

    public static double RADIUS = 1;

    // instance attrs
    public double x;
    public double y;

    // default constructor
    public Point() {
    }

    // constructor with values
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // constructor to copy from another point
    public Point(Point p) {
        x = p.x;
        y = p.y;
    }

    // constructor for a point using a point and a vector
    public Point(Point p, Vector v) {
        x = p.x + v.x;
        y = p.y + v.y;
    }

    // return distance between two points
    public double distanceTo(Point p) {
        double dx = p.x - x;
        double dy = p.y - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // add points
    public void add(Point p) {
        x += p.x;
        y += p.y;
    }

    // subtract points
    public void subtract(Point p) {
        x -= p.x;
        y -= p.y;
    }

    // rotate the point
    public void rotate(double degrees) {
        double cos = Math.cos(Math.toRadians(degrees));
        double sin = Math.sin(Math.toRadians(degrees));
        double px = x;
        double py = y;

        x = px * cos - py * sin;
        y = py * cos + px * sin;

    }

    // draw the point
    public void draw(Vector scroll) {
        StdDraw.circle(x - scroll.x, y - scroll.y, RADIUS);
    }

    // subtract points

    // to string method
    public String toString() {
        return String.format("(%s, %s)", x, y);
    }

    public static void main(String[] args) {
        // testing point stuff

        Point p1 = new Point();
        Point p2 = new Point(10, 10);
    }
}
