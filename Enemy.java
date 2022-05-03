public class Enemy extends Entity {

    // static vars -------------------------------------------------------------

    private static final double VEL_THRUST_THRESHOLD = 0.75;

    // store pointer to the target (initialized in main game class)
    private static Player target;
    private static Clock clock = new Clock();

    // static methods ----------------------------------------------------------

    // set the target for all enemies
    public static void setTarget(Player e) {
        target = e;
    }

    // start the clock for the enemies
    public static void startClock() {
        clock.start();
    }

    // instance vars -----------------------------------------------------------

    // stat variables
    private double rotationalSpeed = 10;
    private double spd = 1;
    private double size;

    // shooting stuff
    private boolean shot = false;
    private double range;
    private boolean inRange = false;
    private int ammoStat = 5;
    private int ammo = ammoStat;
    private boolean reloading = false;
    private double missAngle = 30;

    // constructors ------------------------------------------------------------

    // constructor values
    public Enemy(double x, double y, double size) {
        super(BasePolygon.genShape(3, size), new Vector(), 90);
        this.size = size;

        double mins = clock.elapsedMins();

        dmg = 10 + mins;
        hp = 45;
        hp += 1.1 * mins;
        head = poly.verts[0];
        poly.recenter(x, y);
        range = size + 30;
        tag = Constants.ENEMY_TAG;
        setPoints(hp / 2 + mins * 1.25);
        dropChance = Math.min(1, 0.1 + 0.3 * mins);
        homeChance = 0.0;
        reloadTime = 2000;
        max_speed = 1.5;
        projSpd = max_speed * 1.25;

        TIMER = new Clock.Timer();
        TIMER.addTimer(Constants.SHOOT_KEY, 500);
        TIMER.addTimer(Constants.RELOAD_KEY, reloadTime);
        TIMER.addTimer(Constants.EXHAUST_KEY, 10);

        if (canDropBuffs()) {
            int drops = (int) (dropChance * 5 + 1);
            genBuffs(drops);
        }
        for (Buff b : buffs) {
            points += b.value;
        }
    }

    // getters -----------------------------------------------------------------

    public boolean getThrusting() {
        return vel.norm() > VEL_THRUST_THRESHOLD;
    }

    // other methods -----------------------------------------------------------

    public boolean canGenExhaust() {
        return getThrusting() && TIMER.checkTimer(Constants.EXHAUST_KEY);
    }

    // return if the enemy can shoot
    public boolean canShoot() {
        return TIMER.silentCheckTimer(Constants.SHOOT_KEY) && ammo > 0 && inRange;
    }

    // return if can shoot homing bullet
    public boolean canShootHoming() {
        return StdRandom.uniform() < homeChance;
    }

    // return if the enemy has shoot
    public boolean hasShot() {
        return shot;
    }

    // shoot method
    public Bullet shoot() {
        Bullet b;
        if (canShootHoming()) {
            b = shootHoming();
        }
        else {
            b = shootNormal();
        }
        ammo--;
        TIMER.setCheck(Constants.SHOOT_KEY);
        vel.subtract(Vector.scale(b.vel, b.area() * 2 / area()));
        return b;
    }

    public Bullet shootNormal() {
        double inaccuracy = StdRandom.uniform() * missAngle - missAngle / 2;
        Bullet b = new Bullet(head, rotation + inaccuracy, projSpd, dmg, tag,
                              Constants.ENEMY_GLOW);
        return b;
    }

    // shoot homing method
    public Homing shootHoming() {
        double inaccuracy = StdRandom.uniform() * missAngle - missAngle / 2;
        Homing h = new Homing(head, rotation + inaccuracy, projSpd, dmg, tag,
                              Constants.ENEMY_GLOW, 90);
        h.setTarget(target);
        h.setHomeWeight(20);
        return h;
    }

    // reload method
    public void reload() {
        ammo = ammoStat;
        reloading = false;
    }

    // translate method
    public void translate(Vector v) {
        poly.translate(v);
    }

    // move method
    public void move(double dt) {
        Point c = center();
        Point tc = target.center();
        inRange = false;
        boolean moving = false;
        // move if the out of range of target
        if (c.distanceTo(tc) > range && !target.isDead()) {
            // rotate towards the target
            Vector v = new Vector(c, tc);
            v.resize(spd * dt);

            // move towards the target
            vel.add(v);
            moving = true;
        }

        if (!moving) {
            // slow down otherwise
            vel.add(-vel.norm() * 0.1 * dt);
            inRange = true;
        }

        vel.clamp(max_speed);
        translate(Vector.scale(vel, dt));
    }

    // update method
    public void update(double dt) {
        shot = false;
        move(dt);

        if (!target.isDead() && inRange) {
            rotateAtTarget(dt);
        }
        else {
            rotate(rotationalSpeed * dt);
        }

        // shoot at the target
        if (canShoot() && !target.isDead()) {
            shot = true;
        }
        else if (TIMER.checkTimer(Constants.RELOAD_KEY) && reloading) {
            reload();
        }
        else if (ammo == 0 && !reloading) {
            reloading = true;
            TIMER.setCheck(Constants.RELOAD_KEY);
        }
    }

    // rotate target method
    public void rotateAtTarget(double dt) {
        // rotate towards the target if needed
        Vector v = new Vector(center(), target.center());
        double angle = v.angleDeg();

        if (angle < 0) {
            angle = 360 + angle;
        }

        if (v.angle(vel) > Constants.TOLERANCE) {
            double rotateAngle = (angle - rotation) * dt;
            rotate(rotateAngle);
        }
    }

    // draw method
    public void draw(Vector scroll) {
        // under glow
        Point c = center();
        c.subtract(scroll);

        double[] x = poly.xVerts(scroll.x);
        double[] y = poly.yVerts(scroll.y);
        VFX.glow(x, y, Constants.ENEMY_GLOW, 1);

        super.draw(scroll);

        // draw line behind
        StdDraw.setPenColor(Constants.PRIMARY_COLOR);
        double cos = Math.cos(Math.toRadians(rotation));
        double sin = Math.sin(Math.toRadians(rotation));
        double d = getRadius() * 2;
        double l = getRadius() / 2;
        Point p1 = new Point(head.x - d * cos - l * sin,
                             head.y - d * sin + l * cos);
        Point p2 = new Point(head.x - d * cos + l * sin,
                             head.y - d * sin - l * cos);
        p1.subtract(scroll);
        p2.subtract(scroll);
        StdDraw.line(p1.x, p1.y, p2.x, p2.y);
    }

    public static void main(String[] args) {

    }
}
