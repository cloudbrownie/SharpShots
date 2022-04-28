import java.awt.Color;

public class Enemy extends Entity {

    // maximum speed of an enemy
    private final double MAX_SPEED = 1;

    // store pointer to the target (initialized in main game class)
    private static Entity target;

    // stat variables
    private double str;
    private double projSpd;
    private boolean isThrusting;
    private boolean isTurningRight;
    private boolean isTurningLeft;
    private double rotationalSpeed;
    private double spd;
    private Point head;
    private double size;
    private boolean shot;
    private double shootCooldown;
    private double lasShotTime;
    private Color glow;

    // set the target for all enemies
    public static void setTarget(Entity e) {
        target = e;
    }

    // default constructor
    public Enemy() {
    }

    // constructor values
    public Enemy(double x, double y, double size) {
        str = 5;
        projSpd = MAX_SPEED * 1.25;
        isThrusting = false;
        isTurningRight = false;
        isTurningLeft = false;
        shot = false;
        shootCooldown = 500;
        lasShotTime = 0;
        rotationalSpeed = 10;
        spd = 1;
        tag = 'e';

        // create polygon
        this.size = size;
        poly = new CollidablePolygon(BasePolygon.generateShape(5, size));
        poly.recenter(x, y);
        head = poly.verts[1];
        vel = new Vector();
        hp = 10;
        rotation = 90;
        glow = new Color(255, 0, 0, 5);
    }

    // move method
    public void move(double dt) {

    }

    // shoot method
    public void shoot() {

    }

    // draw method
    public void draw(Vector scroll) {
        super.draw(scroll);
    }

    public static void main(String[] args) {

    }
}
