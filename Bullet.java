import java.awt.Color;
import java.util.ArrayList;

public class Bullet extends Entity {

    // statics vars ------------------------------------------------------------

    // default bullet size
    public static final double BASE_SIZE = 2;

    // instance vars -----------------------------------------------------------

    // bullet attributes
    protected Point pos;
    protected int sides = 4;
    protected double size;
    protected double angle;
    protected Color glow;

    // constructors ------------------------------------------------------------

    // default constructor
    public Bullet() {
    }

    // basic constructor
    public Bullet(Point pos, double degrees, double speed, double dmg,
                  char ownerTag, Color glow) {

        this.pos = new Point(pos);
        this.glow = glow;
        this.dmg = dmg;

        angle = degrees;
        vel = new Vector(speed * Math.cos(Math.toRadians(angle)),
                         speed * Math.sin(Math.toRadians(angle)));
        size = BASE_SIZE;
        poly = new CollidablePolygon(BasePolygon.genShape(sides, size / 2));
        poly.recenter(pos);
        hp = 1;
        tag = ownerTag;
    }

    // getters -----------------------------------------------------------------

    public Point getPos() {
        return pos;
    }

    public int getSides() {
        return sides;
    }

    public double getDmg() {
        return dmg;
    }

    public double getSize() {
        return size;
    }

    public double getAngle() {
        return angle;
    }

    public Color getGlow() {
        return glow;
    }

    // setters -----------------------------------------------------------------

    public void setPos(Point pos) {
        this.pos = new Point(pos);
    }

    public void setSides(int sides) {
        this.sides = sides;
    }

    public void setDmg(double dmg) {
        this.dmg = dmg;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setGlow(Color g) {
        glow = new Color(g.getRed(), g.getBlue(), g.getGreen(), g.getAlpha());
    }

    // other methods -----------------------------------------------------------

    // collide method with other bullets
    public boolean collide(Bullet b) {
        return poly.collide(b.poly).isNonZero();
    }

    // area method
    public double area() {
        return poly.area() / 2;
    }

    // return the center position
    public Point center() {
        return poly.center();
    }

    // collide method
    public Vector collide(Entity e) {
        if (!e.canBeDamaged()) {
            return new Vector();
        }

        Vector mtv = super.collide(e);
        if (mtv.isNonZero()) {
            hp--;
            return mtv;
        }
        return new Vector();
    }

    // used by subclass
    public void homeOnTarget(ArrayList<Entity> e, double dt) {
    }

    // draw method
    public void draw(Vector scroll) {
        // create scrolled point
        Point c = center();
        c.subtract(scroll);

        // draw bullet
        StdDraw.setPenColor(Constants.PRIMARY_COLOR);
        StdDraw.circle(c.x, c.y, size / 2);

        // draw the glow effect
        VFX.glow(c, size * 1.1, glow, 1);
    }

    public static void main(String[] args) {

    }
}
