import java.awt.Color;

public class Bullet extends Entity {

    // position of the bullet
    private Point pos;
    private Point orig;

    // normal bullet uses collidablecircle instead of polygon
    CollidableCircle circ;

    // bullet damage
    public double dmg;

    // default bullet size
    private static final double BASE_SIZE = 1.5;

    // bullet size
    private double size;

    // homing boolean
    private boolean homing;

    // angle used for homing bullets
    private double angle;

    // glow color
    private Color glow;

    // default constructor
    public Bullet() {
    }

    // basic constructor
    public Bullet(Point pos, double degrees, double speed, double dmg,
                  boolean homing, char ownerTag, Color glow) {
        orig = new Point(pos);
        this.pos = new Point(pos);

        angle = degrees;
        vel = new Vector(speed * Math.cos(Math.toRadians(angle)),
                         speed * Math.sin(Math.toRadians(angle)));

        this.dmg = dmg;
        size = BASE_SIZE;
        this.homing = homing;

        circ = new CollidableCircle(pos, size);
        hp = 1;
        tag = ownerTag;
        this.glow = glow;
    }

    // return the center position
    public Point center() {
        return pos;
    }

    // return the size of the bullet
    public double getSize() {
        return size;
    }

    // area method
    public double area() {
        return circ.area() / 10;
    }

    // translate method
    public void translate(Vector v) {
        pos.add(v);
        circ.translate(v);
    }

    // move method
    public void move(double dt) {
        translate(Vector.scale(vel, dt));
    }

    // update method
    public void update(double dt) {
        move(dt);
    }

    // draw method
    public void draw(Vector scroll) {
        Color save = StdDraw.getPenColor();

        // create scrolled point
        Point c = new Point(pos.x - scroll.x, pos.y - scroll.y);

        // draw the glow effect
        VFX.glow(c.x, c.y, size * 3, glow, 1);

        // draw fill color
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledCircle(c.x, c.y, size / 2);

        // draw outline
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.circle(c.x, c.y, size / 2);

        // return pen color to previous
        StdDraw.setPenColor(save);
    }

    // collide method (uses circle)
    public Vector collide(Entity e) {
        Vector mtv = circ.collide(e.poly);
        if (mtv.isNonZero()) {
            hp--;
        }
        return mtv;
    }


    public static void main(String[] args) {

    }
}
