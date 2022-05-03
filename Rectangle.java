public class Rectangle {

    // instance vars -----------------------------------------------------------

    // store x and y values for the left, right, top, and bottom sides
    private double lft;
    private double rht;
    private double top;
    private double bot;

    // constructors ------------------------------------------------------------

    // constructor to make a rectangle with known side locations
    public Rectangle(double lft, double rht, double top, double bot) {
        this.lft = lft;
        this.rht = rht;
        this.top = top;
        this.bot = bot;
    }

    // constructor to make a rectangle that encompasses points
    public Rectangle(Point[] points) {
        lft = points[0].x;
        rht = points[0].x;
        top = points[0].y;
        bot = points[0].y;
        for (Point p : points) {
            encompass(p);
        }
    }

    // constructor to make a rectangle that encompasses x and y points
    public Rectangle(double[] x, double[] y) {
        lft = x[0];
        rht = x[0];
        top = y[0];
        bot = y[0];
        for (int i = 1; i < x.length; i++) {
            encompass(x[i], y[i]);
        }
    }

    // constructor that copies another rectangle
    public Rectangle(Rectangle r) {
        lft = r.lft;
        rht = r.rht;
        top = r.top;
        bot = r.bot;
    }

    // getters -----------------------------------------------------------------

    public double getLft() {
        return lft;
    }

    public double getRht() {
        return rht;
    }

    public double getTop() {
        return top;
    }

    public double getBot() {
        return bot;
    }

    // other methods -----------------------------------------------------------

    // return boolean if rectangle contains a point
    public boolean contains(Point p) {
        return p.x > lft && p.x < rht && p.y < top && p.y > bot;
    }

    // return the width of the rectangle
    public double width() {
        return rht - lft;
    }

    // return the height of the rectangle
    public double height() {
        return top - bot;
    }

    // return the area of the rectangle
    public double area() {
        return width() * height();
    }

    // return the center of the rectangle
    public Point center() {
        return new Point(lft + width() / 2, bot + height() / 2);
    }

    // return the overlap rectangle of two rectangles
    public Rectangle overlap(Rectangle that) {
        return new Rectangle(Math.max(lft, that.lft), Math.min(rht, that.rht),
                             Math.min(top, that.top), Math.max(bot, that.bot));
    }

    // return boolean of the collision between two rectangles
    public boolean collide(Rectangle that) {
        Rectangle rect = overlap(that);
        return !(rect.rht - rect.lft < 0 || rect.top - rect.bot < 0);
    }

    // translate method
    public void translate(double dx, double dy) {
        lft = lft + dx;
        rht = rht + dx;
        top = top + dy;
        bot = bot + dy;
    }

    // change the points of the rectangle to allow it to encompass a new point
    public void encompass(Point p) {
        lft = Math.min(lft, p.x);
        rht = Math.max(rht, p.x);
        top = Math.max(top, p.y);
        bot = Math.min(bot, p.y);
    }

    // change the points of the rectangle to allow it to encompass a new x and y
    public void encompass(double x, double y) {
        lft = Math.min(lft, x);
        rht = Math.max(rht, x);
        top = Math.max(top, y);
        bot = Math.min(bot, y);
    }

    // change the bounding box by changing what it encompasses
    public void reencompass(Point[] points) {
        lft = points[0].x;
        rht = points[0].x;
        top = points[0].y;
        bot = points[0].y;
        for (Point p : points) {
            encompass(p);
        }
    }

    // draw method
    public void draw(Vector scroll) {
        double[] x = { lft, lft, rht, rht };
        double[] y = { bot, top, top, bot };
        for (int i = 0; i < x.length; i++) {
            x[i] -= scroll.x;
            y[i] -= scroll.y;
        }
        StdDraw.polygon(x, y);
    }

    // debug drawing method
    public void drawDebug(Vector scroll) {
        draw(scroll);
        StdDraw.circle(lft + width() / 2, bot + height() / 2, 0.1);
    }

    public String toString() {
        return String.format("(%.2f, %.2f), (%.2f, %.2f)", lft, top, rht, bot);
    }

    public static void main(String[] args) {

    }
}
