import java.awt.Color;
import java.awt.event.KeyEvent;

public class BasePolygon {

    // store default outline color
    public static final Color DEFAULT_OUTLINE = Color.BLACK;

    // generators for basic shapes
    public static BasePolygon generateShape(int sides, double size) {
        double currDegrees = 0;
        double degreeIncr = 360 / sides;

        double[] x = new double[sides];
        double[] y = new double[sides];

        for (int i = 0; i < sides; i++) {
            x[i] = size * Math.cos(Math.toRadians(currDegrees));
            y[i] = size * Math.sin(Math.toRadians(currDegrees));
            currDegrees += degreeIncr;
        }
        return new BasePolygon(x, y);
    }

    // store number of vertices
    public int n;

    // store vertices
    protected Point[] verts;

    // store edges
    private Vector[] edges;

    // store normal vectors to each edge
    private Vector[] edgeNorms;

    // default constructor
    public BasePolygon() {
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

    // return the centroid of the polygon as a point
    public Point centroid() {
        Point c = new Point(0, 0);
        for (Point p : verts) {
            c.x += p.x;
            c.y += p.y;
        }
        c.x /= n;
        c.y /= n;
        return c;
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

    // reposition the polygon to a new x and y for its centroid
    public void setCentroid(double x, double y) {
        Point c = centroid();
        for (Point p : verts) {
            p.x += x - c.x;
            p.y += y - c.y;
        }
        generateVectors();
    }

    // reposition the polygon to a new point for its centroid
    public void setCentroid(Point p) {
        Point c = centroid();
        for (Point v : verts) {
            v.x += p.x - c.x;
            v.y += p.y - c.y;
        }
        generateVectors();
    }

    // translate method
    public void translate(double dx, double dy) {
        for (Point p : verts) {
            p.x += dx;
            p.y += dy;
        }
        generateVectors();
    }

    // translate method with vector
    public void translate(Vector v) {
        for (Point p : verts) {
            p.x += v.x;
            p.y += v.y;
        }
        generateVectors();
    }

    // rotate method using degrees
    public void rotate(double degrees) {
        double cos = Math.cos(Math.toRadians(degrees));
        double sin = Math.sin(Math.toRadians(degrees));
        Point c = centroid();
        for (Point p : verts) {
            double px = p.x - c.x;
            double py = p.y - c.y;
            p.x = px * cos - py * sin + c.x;
            p.y = py * cos + px * sin + c.y;
        }
        generateVectors();
    }

    // return the normal vector to the collision, will be 0, 0 if no collision
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
                smallest.copy(axis);
            }
        }

        // flip smallest axis if it is pointing in the wrong direction
        Point ca = centroid();
        Point cb = b.centroid();
        Vector direction = new Vector(cb.x, cb.y, ca.x, ca.y);
        if (smallest.dot(direction) < 0) {
            smallest.scale(-1);
        }

        // return mtv
        return Vector.scale(smallest, minOverlap);
    }

    // return minimum translation vector if this polygon collides with a circle
    public Vector collide(CollidableCircle c) {

        // minimum translation vector needed to separate two polygons
        double minOverlap = Double.POSITIVE_INFINITY;
        Vector smallest = new Vector();

        // test all axes
        for (Vector axis : edgeNorms) {

            // project this polygon onto axis
            double maxA = axis.dot(verts[0]);
            double minA = axis.dot(verts[0]);
            for (int i = 1; i < n; i++) {
                double projection = axis.dot(verts[i]);
                maxA = Math.max(maxA, projection);
                minA = Math.min(minA, projection);
            }

            // project circle onto axis
            double maxB = axis.dot(c.center) + c.radius;
            double minB = axis.dot(c.center) - c.radius;

            // no overlap, return a 0 mtv
            if (minA > maxB || minB > maxA) {
                return new Vector();
            }

            // find smallest overlap and corresponding axis
            double overlap = Math.min(maxA - minB, maxB - minA);
            if (overlap < minOverlap) {
                minOverlap = overlap;
                smallest.copy(axis);
            }
        }

        // find the axis to test against: centroid to circle center
        Vector axis = new Vector(centroid(), c.center);
        axis.normalize();

        // project polygon and circle onto axis
        double maxA = axis.dot(verts[0]);
        double minA = axis.dot(verts[0]);
        for (int i = 1; i < n; i++) {
            double projection = axis.dot(verts[i]);
            maxA = Math.max(maxA, projection);
            minA = Math.min(minA, projection);
        }

        double maxB = axis.dot(c.center) + c.radius;
        double minB = axis.dot(c.center) - c.radius;

        // no overlap
        if (minA > maxB || minB > maxA) {
            return new Vector();
        }

        // find smallest overlap and corresponding axis
        double overlap = Math.min(maxB - minA, maxA - minB);
        if (overlap < minOverlap) {
            minOverlap = overlap;
            smallest.copy(axis);
        }

        // flip smallest axis if it is pointing in the wrong direction
        Point ca = centroid();
        Point cb = c.center;
        Vector direction = new Vector(cb.x, cb.y, ca.x, ca.y);
        if (smallest.dot(direction) < 0) {
            smallest.scale(-1);
        }

        // return mtv
        return Vector.scale(smallest, minOverlap);
    }

    // draws the basic polygon
    public void draw(Vector scroll) {
        Color save = StdDraw.getPenColor();

        StdDraw.setPenColor(DEFAULT_OUTLINE);
        StdDraw.polygon(xVerts(scroll.x), yVerts(scroll.y));

        StdDraw.setPenColor(save);
    }

    // draws the basic polygon with custom fill color
    public void draw(Vector scroll, Color fill) {
        Color save = StdDraw.getPenColor();

        double[] x = xVerts(scroll.x);
        double[] y = yVerts(scroll.y);

        // draw the fill color
        StdDraw.setPenColor(fill);
        StdDraw.filledPolygon(x, y);

        // draw outline
        StdDraw.setPenColor(DEFAULT_OUTLINE);
        StdDraw.polygon(x, y);

        StdDraw.setPenColor(save);
    }

    // draws the basic polygon with custom fill and outline colors
    public void draw(Vector scroll, Color fill, Color outline) {
        Color save = StdDraw.getPenColor();

        double[] x = xVerts(scroll.x);
        double[] y = yVerts(scroll.y);

        // draw the fill color
        StdDraw.setPenColor(fill);
        StdDraw.filledPolygon(x, y);

        // draw the outline
        StdDraw.setPenColor(outline);
        StdDraw.polygon(x, y);

        StdDraw.setPenColor(save);
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
        Point c = centroid();
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
            Point start = new Point(center, Vector.scale(norm, 0.5));
            Point end = new Point(center, Vector.scale(norm, -0.5));
            StdDraw.line(start.x - scroll.x, start.y - scroll.y,
                         end.x - scroll.x, end.y - scroll.y);
            norm.drawDebug(scroll);
        }
    }

    // default test script
    public static void defaultTest(double scale) {
        double[] x1 = { 10, 60, 30, 10 };
        double[] y1 = { 20, 40, 60, 50 };
        BasePolygon a = new BasePolygon(x1, y1);

        double[] x2 = { 5, 20, 40, 5 };
        double[] y2 = { 10, 5, 20, 70 };
        BasePolygon b = new BasePolygon(x2, y2);

        a.setCentroid(45, 50);

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

            StdDraw.show();
        }
    }

    // square and circle test
    public static void sqcircTest(double scale) {
        double size = scale * 0.1;
        double cx = scale * 0.5;
        double cy = scale * 0.5;
        double[] sqx = { cx - size, cx + size, cx + size, cx - size };
        double[] sqy = { cy - size, cy - size, cy + size, cy + size };
        BasePolygon a = new BasePolygon(sqx, sqy);

        CollidableCircle b = new CollidableCircle(cx - size * 2, cy - size * 2, size);

        Clock clock = new Clock();

        double speed = scale * 0.03;

        Vector scroll = new Vector();

        clock.start();
        while (true) {

            double dt = clock.tick();

            StdDraw.clear();

            // find movement speed for
            double frameSpeed = speed * dt;

            // get input
            if (StdDraw.hasNextKeyTyped()) {
                if (StdDraw.isKeyPressed(KeyEvent.VK_W)) {
                    a.translate(0, frameSpeed);
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_A)) {
                    a.translate(-frameSpeed, 0);
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_S)) {
                    a.translate(0, -frameSpeed);
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_D)) {
                    a.translate(frameSpeed, 0);
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_Q)) {
                    a.rotate(5 * dt);
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_E)) {
                    a.rotate(-5 * dt);
                }
            }

            Vector mtv = a.collide(b);
            if (mtv.isNonZero()) {
                a.translate(Vector.scale(mtv, 0.5));
                b.translate(Vector.scale(mtv, -0.5));
            }

            a.draw(scroll);
            b.draw(scroll);

            StdDraw.show();
        }
    }

    public static void main(String[] args) {

        double scale = 100;
        StdDraw.setScale(0, scale);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenRadius(0.005);

        if (args.length == 0) {
            defaultTest(scale);
        }

        else {
            if (args[0].equals("SQUARES")) {
                squareTest(scale);
            }
            else if (args[0].equals("SQCIRC")) {
                sqcircTest(scale);
            }
        }
    }
}
