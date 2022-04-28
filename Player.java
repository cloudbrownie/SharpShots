import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Player extends Entity {

    // maximum speed of the player
    private final double MAX_SPEED = 2;

    // number of lives for the player
    private int lives = 5;

    // base damage of the player's projectiles
    private double str;

    // base speed of the player's projectile
    private double projSpd;

    // boolean of if the player is thrusting
    private boolean isThrusting;

    // boolean of if player is turning right
    private boolean isTurningRight;

    // boolean of if player is turning left
    private boolean isTurningLeft;

    // rotational speed of the player
    private double rotationalSpeed;

    // speed of the player
    private double spd;

    // store pointer to head of the ship
    private Point head;

    // store size of the ship
    private double size;

    // shooting boolean
    private boolean shot;

    // shooting cool down vars
    private double shootCooldown;
    private double lastShotTime;

    // projectile glow color
    private Color glow;

    // default constructor
    public Player(double x, double y, double size) {
        str = 10;
        projSpd = MAX_SPEED * 1.25;
        isThrusting = false;
        isTurningRight = false;
        isTurningLeft = false;
        shot = false;
        shootCooldown = 150;
        lastShotTime = 0;
        rotationalSpeed = 10;
        spd = 1;
        tag = 'p';

        // create player polygon
        this.size = size;
        double[] px = { x - size, x, x + size };
        double[] py = { y - size, y + size, y - size };
        poly = new CollidablePolygon(px, py);
        head = poly.verts[1];
        vel = new Vector();
        hp = 20;
        rotation = 90;
        glow = new Color(0, 255, 0, 5);
    }

    // check if can shoot method
    private boolean canShoot() {
        return System.currentTimeMillis() - lastShotTime > shootCooldown;
    }

    // check if the player has shot
    public boolean hasShot() {
        return shot;
    }

    // shooting method
    public Bullet shoot() {
        return new Bullet(head, rotation, projSpd, str, false, tag, glow);
    }

    // handle movement inputs for the player
    public void handleInputs() {
        isThrusting = false;
        isTurningLeft = false;
        isTurningRight = false;
        shot = false;

        // check if player isnt dead
        if (!isDead()) {
            // check if player is applying thrust
            if (StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
                isThrusting = true;
            }

            // check if the player is trying to turn
            if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {
                isTurningLeft = true;
            }
            if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
                isTurningRight = true;
            }

            // check if the player is trying to shoot
            if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                if (canShoot()) {
                    shot = true;
                    lastShotTime = System.currentTimeMillis();
                }
            }
        }
    }

    // update method
    public void update(double dt) {
        // move player forward
        if (isThrusting) {
            vel.add(spd * dt, rotation);
            vel.clamp(MAX_SPEED);
        }
        else {
            vel.add(-vel.norm() * 0.1 * dt);
        }

        // turn the ship
        if (isTurningRight) {
            rotate(-rotationalSpeed * dt);
        }
        if (isTurningLeft) {
            rotate(rotationalSpeed * dt);
        }

        // move the ship
        move(dt);
    }

    // die method
    public void die() {
        hp = 0;
        lives--;
    }

    // draw method
    public void draw(Vector scroll) {
        if (!isDead()) {
            // draw the polygon
            super.draw(scroll);

            // draw arrow indicator for the head
            double len = this.size / 2;
            double sub = 3;
            double angle = Math.toRadians(rotation);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            Point c = poly.centroid();

            Point r1 = new Point(head.x - (len - sub) * cos - (len - sub) * sin,
                                 head.y - (len - sub) * sin + (len - sub) * cos);

            Point r2 = new Point(head.x - len * cos + len * sin,
                                 head.y - len * sin - len * cos);

            Point l1 = new Point(head.x - (len - sub) * cos + (len - sub) * sin,
                                 head.y - (len - sub) * sin - (len - sub) * cos);

            Point l2 = new Point(head.x - len * cos - len * sin,
                                 head.y - len * sin + len * cos);

            // apply scroll values
            r1.subtract(scroll);
            r2.subtract(scroll);
            l1.subtract(scroll);
            l2.subtract(scroll);

            // draw the indicator
            StdDraw.line(r1.x, r1.y, r2.x, r2.y);
            StdDraw.line(l1.x, l1.y, l2.x, l2.y);
        }
    }

    // check if player is moving
    public boolean getThrusting() {
        return isThrusting;
    }

    public static void main(String[] args) {

        double scale = 100;
        StdDraw.setScale(0, 100);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenRadius(0.005);

        Clock clock = new Clock();

        Vector scroll = new Vector(10, 0);

        Player p = new Player(scale / 2, scale / 2, scale * 0.05);

        ArrayList<Bullet> bullets = new ArrayList<>();

        clock.start();
        while (true) {

            double dt = clock.tick();

            StdDraw.clear();

            p.handleInputs();

            if (p.hasShot()) {
                bullets.add(p.shoot());
            }

            for (Bullet b : bullets) {
                b.update(dt);
            }

            p.update(dt);

            for (Bullet b : bullets) {
                b.draw(scroll);
            }

            p.draw(scroll);

            StdDraw.show();
        }
    }
}
