import java.awt.Color;
import java.awt.event.KeyEvent;

public class CollidablePolygon extends BasePolygon {

    // instance vars -----------------------------------------------------------

    // axis aligned bounding box for cheap collision detection
    protected Rectangle aabb;

    // constructors ------------------------------------------------------------

    // default constructor
    public CollidablePolygon() {
    }

    // constructor for a polygon with points
    public CollidablePolygon(Point[] vertices) {
        super(vertices);
        aabb = new Rectangle(vertices);
    }

    // constructor for a polygon with x and y points
    public CollidablePolygon(double[] x, double[] y) {
        super(x, y);
        aabb = new Rectangle(x, y);
    }

    // constructor for a polygon using a base polygon
    public CollidablePolygon(BasePolygon b) {
        super(b.verts);
        aabb = new Rectangle(b.verts);
    }

    // getters -----------------------------------------------------------------

    public Rectangle getAabb() {
        return aabb;
    }

    // setters -----------------------------------------------------------------

    public void setAabb(Rectangle aabb) {
        this.aabb = new Rectangle(aabb);
    }

    // other methods -----------------------------------------------------------

    // cheap axis aligned bounding box collision test
    public boolean aabbCollide(Rectangle other) {
        return aabb.collide(other);
    }

    // return bounding box area
    public double aabbArea() {
        return aabb.area();
    }

    // collision function with another polygon
    // got the idea from n tutorial a referenec
    public Vector collide(CollidablePolygon otherPoly) {
        // cheap test bounding boxes
        if (!aabbCollide(otherPoly.aabb)) {
            return new Vector();
        }

        // expensive polygonal collision otherwise
        return super.collide(otherPoly);
    }

    // rotate method
    public void rotate(double degrees) {
        super.rotate(degrees);
        aabb.reencompass(verts);
        generateVectors();
    }

    // translate method
    public void translate(double dx, double dy) {
        super.translate(dx, dy);
        aabb.translate(dx, dy);
        generateVectors();
    }

    // translate method with vector
    public void translate(Vector v) {
        super.translate(v);
        aabb.translate(v.x, v.y);
        generateVectors();
    }

    // recenter method
    public void recenter(double x, double y) {
        super.recenter(x, y);
        aabb.reencompass(verts);
    }

    // recenter method with a point
    public void recenter(Point p) {
        super.recenter(p.x, p.y);
        aabb.reencompass(verts);
    }

    // debug drawing method
    public void drawDebug(Vector scroll) {
        super.drawDebug(scroll);
        aabb.drawDebug(scroll);
    }

    public String toString() {
        StringBuilder info = new StringBuilder();

        info.append("Verts: ");
        for (Point p : verts) {
            info.append(String.format("(%.1f, %.1f), ", p.x, p.y));
        }

        return info.toString();
    }

    public static void defaultTest() {

        double scale = Constants.SCALE;
        CollidablePolygon a = new CollidablePolygon(BasePolygon.genShape(3, scale * 0.2));
        CollidablePolygon b = new CollidablePolygon(BasePolygon.genShape(5, scale * 0.2));
        a.recenter(scale * 0.75, scale * 0.75);
        b.recenter(scale * 0.5, scale * 0.55);

        Vector scroll = new Vector();
        Clock clock = new Clock();

        clock.start();
        while (true) {

            double dt = clock.tick();

            double frameSpeed = scale * 0.02 * dt;

            StdDraw.clear();

            StdDraw.setPenColor(Color.BLACK);

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

            a.drawDebug(scroll);
            b.drawDebug(scroll);

            // test aabb collision
            if (a.aabbCollide(b.aabb)) {
                StdDraw.setPenColor(Color.RED);
                a.aabb.draw(scroll);
                b.aabb.draw(scroll);
            }

            Vector mtv = a.collide(b);
            if (mtv.isNonZero()) {
                a.drawOutline(scroll, Color.BLUE);
                b.drawOutline(scroll, Color.BLUE);
            }

            StdDraw.show();
        }

    }

    public static void nTest(int n) {

        CollidablePolygon[] polys = new CollidablePolygon[n];

        double scale = Constants.SCALE;
        double size = scale * 0.05;
        for (int i = 0; i < n; i++) {
            int sides = StdRandom.uniform(3, 8);
            polys[i] = new CollidablePolygon(BasePolygon.genShape(sides, size));

            double nx = StdRandom.uniform() * scale;
            double ny = StdRandom.uniform() * scale;
            polys[i].recenter(nx, ny);
        }

        CollidablePolygon a = polys[0];

        Vector scroll = new Vector();
        Clock clock = new Clock();

        clock.start();
        while (true) {

            double dt = clock.tick();

            double frameSpeed = scale * 0.02 * dt;

            StdDraw.clear();

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

            for (CollidablePolygon poly : polys) {
                poly.draw(scroll);
            }

            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {

                    if (polys[i].aabbCollide(polys[j].aabb)) {
                        StdDraw.setPenColor(Color.RED);
                        polys[i].aabb.draw(scroll);
                        polys[j].aabb.draw(scroll);
                    }

                    Vector mtv = polys[i].collide(polys[j]);
                    if (mtv.isNonZero()) {
                        polys[i].translate(Vector.scale(mtv, 0.5));
                        polys[j].translate(Vector.scale(mtv, -0.5));
                        polys[i].drawOutline(scroll, Color.BLUE);
                        polys[j].drawOutline(scroll, Color.BLUE);
                    }

                    StdDraw.setPenColor(Color.BLACK);
                }
            }


            StdDraw.show();
        }

    }

    public static void main(String[] args) {

        double scale = 100;
        StdDraw.setScale(0, scale);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenRadius(0.005);

        if (args.length > 0) {
            nTest(Integer.parseInt(args[0]));
        }
        else {
            defaultTest();
        }

    }
}
