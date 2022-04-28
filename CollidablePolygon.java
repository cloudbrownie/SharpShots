public class CollidablePolygon extends BasePolygon {

    // axis aligned bounding box for cheap collision detection
    Rectangle aabb;

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
        Point c = centroid();

        setCentroid(x, y);
        aabb.translate(-c.x, -c.y);
    }

    // return bounding box area
    public double aabbArea() {
        return aabb.area();
    }

    // cheap axis aligned bounding box collision test
    public boolean aabbCollide(Rectangle other) {
        return aabb.collide(other);
    }

    // collision function with another polygon
    public Vector collide(CollidablePolygon otherPoly) {
        // cheap test bounding boxes
        if (!aabbCollide(otherPoly.aabb)) {
            return new Vector();
        }

        // expensive polygonal collision otherwise
        return super.collide(otherPoly);
    }

    // collision function with a circle
    public Vector collide(CollidableCircle c) {
        if (!aabbCollide(c.aabb)) {
            return new Vector();
        }

        return super.collide(c);
    }

    // debug drawing method
    public void drawDebug(Vector scroll) {
        super.drawDebug(scroll);
        aabb.drawDebug(scroll);
    }

    public static void main(String[] args) {

        double[] x1 = { 40, 70, 60, 30, 10 };
        double[] y1 = { 30, 40, 60, 90, 40 };
        CollidablePolygon a = new CollidablePolygon(x1, y1);
        a.recenter(0, 0);

        double scale = 100;
        StdDraw.setScale(0, scale);
        StdDraw.enableDoubleBuffering();

        Clock clock = new Clock();

        Vector scroll = new Vector();

        double[] x2 = { 10, 20, 22, 15, 8 };
        double[] y2 = { 30, 30, 39, 45, 39 };
        CollidablePolygon b = new CollidablePolygon(x2, y2);
        Vector vel = new Vector(0.1, 0.1);

        Rectangle c = new Rectangle(10, 20, 10, 20);

        b.translate(10, 10);
        c.translate(10, 10);

        clock.start();
        while (true) {

            double dt = clock.tick();

            StdDraw.clear();

            a.drawDebug(scroll);
            a.rotate(1 * dt);
            a.translate(0.1 * dt, 0.1 * dt);

            b.drawDebug(scroll);
            b.translate(Vector.scale(vel, dt));
            c.draw(scroll);

            StdDraw.show();

        }

    }
}
