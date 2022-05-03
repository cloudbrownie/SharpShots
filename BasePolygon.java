import java.awt.Color;
import java.awt.event.KeyEvent;

public class BasePolygon {

    // static vars -------------------------------------------------------------

    // store default outline color
    public static final Color DEFAULT_OUTLINE = Color.BLACK;

    // static methods ----------------------------------------------------------

    // generators for basic shapes
    public static BasePolygon genShape(int sides, double size) {
        double currDegrees = 0;
        double degreeIncr = 360.0 / sides;

        double[] x = new double[sides];
        double[] y = new double[sides];

        for (int i = 0; i < sides; i++) {
            x[i] = size * Math.cos(Math.toRadians(currDegrees));
            y[i] = size * Math.sin(Math.toRadians(currDegrees));
            currDegrees += degreeIncr;
        }
        return new BasePolygon(x, y);
    }

    // default test script
    public static void defaultTest() {
        double[] x1 = { 15, 60, 30, 15 };
        double[] y1 = { 30, 40, 60, 50 };
        BasePolygon a = new BasePolygon(x1, y1);

        double[] x2 = { 5, 20, 40, 5 };
        double[] y2 = { 10, 5, 20, 70 };
        BasePolygon b = new BasePolygon(x2, y2);

        a.recenter(45, 50);

        Clock clock = new Clock();

        Vector scroll = new Vector();

        clock.start();

        while (true) {

            double dt = clock.tick();

            StdDraw.clear();

            a.rotate(4 * dt);

            a.drawDebug(scroll);
            b.drawDebug(scroll);

            Vector MTV = a.collide(b);
            if (MTV.isNonZero()) {


                Vector aMTV = Vector.scale(MTV, 0.5);
                Vector bMTV = Vector.scale(MTV, -0.5);

                a.translate(aMTV);
                b.translate(bMTV);
            }

            StdDraw.show();
        }
    }

    // square test script
    public static void squareTest(double scale) {

        // create 10 random squares
        int n = 10;
        double size = scale * 0.05;
        BasePolygon[] squares = new BasePolygon[n];
        for (int i = 0; i < n; i++) {
            double cx = StdRandom.uniform() * scale;
            double cy = StdRandom.uniform() * scale;
            double[] x = { cx - size, cx + size, cx + size, cx - size };
            double[] y = { cy - size, cy - size, cy + size, cy + size };
            squares[i] = new BasePolygon(x, y);
        }

        // movement speed for controllable square
        double speed = scale * 0.02;

        Clock clock = new Clock();

        Vector scroll = new Vector();

        // test loop
        clock.start();
        while (true) {
            StdDraw.clear();

            // find the delta time between frames
            double dt = clock.tick();

            // find movement speed for
            double frameSpeed = speed * dt;

            if (StdDraw.hasNextKeyTyped()) {
                if (StdDraw.isKeyPressed(KeyEvent.VK_W)) {
                    squares[0].translate(0, frameSpeed);
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_A)) {
                    squares[0].translate(-frameSpeed, 0);
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_S)) {
                    squares[0].translate(0, -frameSpeed);
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_D)) {
                    squares[0].translate(frameSpeed, 0);
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_Q)) {
                    squares[0].rotate(5 * dt);
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_E)) {
                    squares[0].rotate(-5 * dt);
                }
            }

            for (int i = 0; i < n - 1; i++) {
                BasePolygon a = squares[i];

                for (int j = i + 1; j < n; j++) {
                    BasePolygon b = squares[j];

                    Vector mtv = a.collide(b);
                    if (mtv.isNonZero()) {
                        a.translate(Vector.scale(mtv, 0.5));
                        b.translate(Vector.scale(mtv, -0.5));
                    }
                }
            }

            for (BasePolygon square : squares) {
                square.draw(scroll);
            }
            squares[0].drawOutline(scroll, Color.RED);

            StdDraw.show();
        }
    }

    // instance vars -----------------------------------------------------------

    // store number of vertices
    private int n;

    // store circumscribe radius
    private double radius;

    // store vertices
    protected Point[] verts;

    // store edges
    protected Vector[] edges;

    // store normal vectors to each edge
    protected Vector[] edgeNorms;

    // constructors ------------------------------------------------------------

    // default constructor
    public BasePolygon() {
    }

    public BasePolygon(int sides, double size) {
        double currDegrees = 0;
        double degreeIncr = 360.0 / sides;

        double[] x = new double[sides];
        double[] y = new double[sides];

        for (int i = 0; i < sides; i++) {
            x[i] = size * Math.cos(Math.toRadians(currDegrees));
            y[i] = size * Math.sin(Math.toRadians(currDegrees));
            currDegrees += degreeIncr;
        }

        n = x.length;
        verts = new Point[n];
        edges = new Vector[n];
        edgeNorms = new Vector[n];

        for (int i = 0; i < n; i++) {
            verts[i] = new Point(x[i], y[i]);
        }

        generateVectors();
    }

    // constructor with points inputted with parallel arrays
    // points should be in order of connecting the dots
    public BasePolygon(double[] x, double[] y) {
        // throw exception if arrays lengths don't match
        if (x.length != y.length) {
            throw new RuntimeException("X and Y array lengths do not match.");
        }

        // instantiate the arrays
        n = x.length;
        verts = new Point[n];
        edges = new Vector[n];
        edgeNorms = new Vector[n];

        // create points for each vertex
        for (int i = 0; i < n; i++) {
            verts[i] = new Point(x[i], y[i]);
        }

        // generate other polygon info
        generateVectors();
    }

    // same as constructor above, but given points
    public BasePolygon(Point[] points) {
        // instantiate the arrays
        n = points.length;
        verts = new Point[n];
        edges = new Vector[n];
        edgeNorms = new Vector[n];

        // create points for each vertex
        for (int i = 0; i < n; i++) {
            verts[i] = new Point(points[i]);
        }

        // generate other polygon info
        generateVectors();
    }

    // getters -----------------------------------------------------------------

    public double getRadius() {
        return radius;
    }


    public int getN() {
        return n;
    }

    public Point[] getVerts() {
        return verts;
    }

    // setters -----------------------------------------------------------------

    public void setN(int n) {
        this.n = n;
    }

    public void setVerts(Point[] verts) {
        n = verts.length;
        this.verts = new Point[n];
        for (int i = 0; i < n; i++) {
            this.verts[i] = verts[i];
        }
    }

    // other methods -----------------------------------------------------------

    // generate edges and normal vectors
    protected void generateVectors() {
        // create edges and normals from each point to the next
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            // generate edge vector, origin = p1
            edges[i] = new Vector(verts[i], verts[j]);
            // generate normal vector to edge vector, origin = middle of edge
            Point edgeCenter = new Point(verts[i], Vector.scale(edges[i], 0.5));
            Vector normal = edges[i].getPerpendicular();
            normal.setOrigin(edgeCenter);
            normal.normalize();
            normal.scale(-1);
            edgeNorms[i] = normal;
        }

        radius = 0;
        Point c = center();
        for (Point p : verts) {
            radius = Math.max(radius, c.distanceTo(p));
        }
    }

    // return the area of the polygon
    public double area() {
        double area = 0;

        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;

            area += verts[i].x * verts[j].y - verts[i].y * verts[j].x;
        }

        return Math.abs(area / 2);
    }

    // return the x coordinates for each vertex, can apply a scroll value
    public double[] xVerts(double scroll) {
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = verts[i].x - scroll;
        }
        return x;
    }

    // return the y coordinates for each vertex, can apply a scroll value
    public double[] yVerts(double scroll) {
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            y[i] = verts[i].y - scroll;
        }
        return y;
    }

    // return the center
    public Point center() {
        Point c = new Point(0, 0);
        for (Point p : verts) {
            c.x = c.x + p.x;
            c.y = c.y + p.y;
        }
        c.x = c.x / n;
        c.y = c.y / n;
        return c;
    }

    // return the normal vector to the collision, will be 0, 0 if no collision
    // got the concept from n tutorial a reference
    public Vector collide(BasePolygon b) {

        // minimum translation vector needed to separate two polygons
        double minOverlap = Double.POSITIVE_INFINITY;
        Vector smallest = new Vector();

        // gather all test axes
        Vector[] test = new Vector[n + b.n];
        for (int i = 0; i < n; i++) {
            test[i] = edgeNorms[i];
        }
        for (int i = 0; i < b.n; i++) {
            test[i + n] = b.edgeNorms[i];
        }

        // test all axes
        for (Vector axis : test) {

            // project this polygon onto axis
            double maxA = axis.dot(verts[0]);
            double minA = axis.dot(verts[0]);
            for (int i = 1; i < n; i++) {
                double projection = axis.dot(verts[i]);
                maxA = Math.max(maxA, projection);
                minA = Math.min(minA, projection);
            }

            // project that polygon onto axis
            double maxB = axis.dot(b.verts[0]);
            double minB = axis.dot(b.verts[0]);
            for (int i = 1; i < b.n; i++) {
                double projection = axis.dot(b.verts[i]);
                maxB = Math.max(maxB, projection);
                minB = Math.min(minB, projection);
            }

            // no overlap, return a 0 mtv
            if (minA > maxB || minB > maxA) {
                return new Vector();
            }

            // find smallest overlap and corresponding axis
            double overlap = Math.min(maxA - minB, maxB - minA);
            if (overlap < minOverlap) {
                minOverlap = overlap;
                smallest = axis;
            }
        }

        // flip smallest axis if it is pointing in the wrong direction
        Point ca = center();
        Point cb = b.center();
        Vector direction = new Vector(cb, ca);
        Vector mtv = new Vector(smallest);
        if (mtv.dot(direction) < 0) {
            mtv.scale(-1);
        }

        // return mtv
        return Vector.scale(mtv, minOverlap);
    }

    // reposition the polygon to a new x and y for its centroid
    public void recenter(double x, double y) {
        Point c = center();
        for (Point p : verts) {
            p.x = p.x + x - c.x;
            p.y = p.y + y - c.y;
        }
        generateVectors();
    }

    // reposition the polygon to a new point for its centroid
    public void recenter(Point p) {
        Point c = center();
        for (Point v : verts) {
            v.x = v.x + p.x - c.x;
            v.y = v.y + p.y - c.y;
        }
        generateVectors();
    }

    // translate method
    public void translate(double dx, double dy) {
        for (Point p : verts) {
            p.x = p.x + dx;
            p.y = p.y + dy;
        }
        generateVectors();
    }

    // translate method with vector
    public void translate(Vector v) {
        for (Point p : verts) {
            p.x = p.x + v.x;
            p.y = p.y + v.y;
        }
        generateVectors();
    }

    // rotate method using degrees
    public void rotate(double degrees) {
        double cos = Math.cos(Math.toRadians(degrees));
        double sin = Math.sin(Math.toRadians(degrees));
        Point c = center();
        for (Point p : verts) {
            double px = p.x - c.x;
            double py = p.y - c.y;
            p.x = px * cos - py * sin + c.x;
            p.y = py * cos + px * sin + c.y;
        }
        generateVectors();
    }

    // draws the basic polygon
    public void draw(Vector scroll) {
        StdDraw.setPenColor(DEFAULT_OUTLINE);
        StdDraw.polygon(xVerts(scroll.x), yVerts(scroll.y));
    }

    // draws the basic polygon with custom fill color
    public void draw(Vector scroll, Color fill) {

        // draw the fill color
        drawFill(scroll, fill);

        // draw outline
        drawOutline(scroll, DEFAULT_OUTLINE);
    }

    // draws the basic polygon with custom fill and outline colors
    public void draw(Vector scroll, Color fill, Color outline) {

        // draw the fill color
        drawFill(scroll, fill);

        // draw the outline
        drawOutline(scroll, outline);
    }

    // draws just the filled polygon
    public void drawFill(Vector scroll, Color fill) {
        double[] x = xVerts(scroll.x);
        double[] y = yVerts(scroll.y);

        // draw the filled
        StdDraw.setPenColor(fill);
        StdDraw.filledPolygon(x, y);

    }

    // draws just the outline polygon
    public void drawOutline(Vector scroll, Color outline) {
        double[] x = xVerts(scroll.x);
        double[] y = yVerts(scroll.y);

        StdDraw.setPenColor(outline);
        StdDraw.polygon(x, y);
    }

    // draws the edges as vectors
    public void drawVectors(Vector scroll) {
        for (Vector edge : edges) {
            edge.drawDebug(scroll);
        }
    }

    // draws just the points of the polygon
    public void drawVertices(Vector scroll) {
        for (Point p : verts) {
            StdDraw.circle(p.x - scroll.x, p.y - scroll.y, 0.5);
        }
    }

    // draws just the centroid of the polygon
    public void drawCentroid(Vector scroll) {
        Point c = center();
        StdDraw.circle(c.x - scroll.x, c.y - scroll.y, 0.5);
    }

    // draws the polygon with the normal vectors on each edge
    public void drawDebug(Vector scroll) {
        drawVectors(scroll);
        drawVertices(scroll);
        drawCentroid(scroll);

        // draw a dash through every edge to represent the normal for each edge
        for (Vector norm : edgeNorms) {
            Point center = norm.getOrigin();
            StdDraw.circle(center.x, center.y, 1);
            Point start = new Point(center, Vector.scale(norm, 0.5));
            Point end = new Point(center, Vector.scale(norm, -0.5));
            StdDraw.line(start.x - scroll.x, start.y - scroll.y,
                         end.x - scroll.x, end.y - scroll.y);
            norm.drawDebug(scroll);
        }
    }

    public static void main(String[] args) {

        double scale = 100;
        StdDraw.setScale(0, scale);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenRadius(0.005);

        if (args.length == 0) {
            defaultTest();
        }

        else {
            if (args[0].equals("SQUARES")) {
                squareTest(scale);
            }
        }
    }
}
