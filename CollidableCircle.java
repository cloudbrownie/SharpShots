import java.awt.event.KeyEvent;

public class CollidableCircle {

    // axis aligned bounding box for cheap collisions
    public Rectangle aabb;

    // center point
    public Point center;

    // radius value
    public double radius;

    // constructor with a center point and a radius
    public CollidableCircle(Point p, double radius) {
        center = new Point(p);
        this.radius = radius;
        aabb = new Rectangle(p.x - radius, p.x + radius, p.y + radius,
                             p.y - radius);
    }

    // constructor with a x and y center and a radius
    public CollidableCircle(double x, double y, double radius) {
        center = new Point(x, y);
        this.radius = radius;
        aabb = new Rectangle(x - radius, x + radius, y + radius,
                             y - radius);
    }

    // area method
    public double area() {
        return Math.PI * radius * radius;
    }

    // cheap collision test
    public boolean aabbCollide(Rectangle other) {
        return aabb.collide(other);
    }

    // circle colliding with a circle
    public Vector collide(CollidableCircle c) {
        if (!aabbCollide(c.aabb)) {
            return new Vector();
        }

        // find overlap if overlap
        double minDist = radius + c.radius;
        double dx = center.x - c.center.x;
        double dy = center.y - c.center.y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        // collision, return minimum translation vector
        if (dist < minDist) {
            Vector mtv = new Vector(c.center.x, c.center.y, center.x, center.y);
            mtv.normalize();
            mtv.scale(minDist - dist);
            return mtv;
        }

        // reaching here means no collision
        return new Vector();
    }

    // circle colliding with a polygon
    public Vector collide(CollidablePolygon otherPoly) {
        if (!aabbCollide(otherPoly.aabb)) {
            return new Vector();
        }

        // circle collision with a polygon
        return otherPoly.collide(this);
    }

    // translate method
    public void translate(double dx, double dy) {
        center.x += dx;
        center.y += dy;
        aabb.translate(dx, dy);
    }

    // translate method using a vector
    public void translate(Vector v) {
        translate(v.x, v.y);
    }

    // draw method
    public void draw(Vector scroll) {
        StdDraw.circle(center.x - scroll.x, center.y - scroll.y, radius);
    }

    // draw just center
    public void drawCenter(Vector scroll) {
        StdDraw.circle(center.x - scroll.x, center.y - scroll.y, 0.5);
    }

    // debug draw method
    public void drawDebug(Vector scroll) {
        draw(scroll);
        drawCenter(scroll);
        aabb.draw(scroll);
    }

    public static void main(String[] args) {

        double windowScale = 100;
        StdDraw.setScale(0, windowScale);
        StdDraw.enableDoubleBuffering();


        CollidableCircle a = new CollidableCircle(windowScale * 0.1,
                                                  windowScale * 0.1,
                                                  windowScale * 0.1);
        CollidableCircle b = new CollidableCircle(windowScale * 0.5,
                                                  windowScale * 0.5,
                                                  windowScale * 0.1);

        // desired speed
        double speed = windowScale * 0.02;
        double frameSpeed;

        // frame independence var
        double prevTime = System.currentTimeMillis();
        double dt;

        Vector scroll = new Vector();

        // game loop
        while (true) {

            dt = System.currentTimeMillis() - prevTime;
            dt /= 60;
            prevTime = System.currentTimeMillis();

            frameSpeed = speed * dt;

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
            }

            Vector mtv = a.collide(b);
            if (mtv.isNonZero()) {
                a.translate(Vector.scale(mtv, 0.5));
                b.translate(Vector.scale(mtv, -0.5));
            }

            a.draw(scroll);
            b.draw(scroll);

            StdDraw.show();
            StdDraw.pause(20);
        }
    }
}
