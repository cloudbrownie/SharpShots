import java.awt.Color;
import java.awt.event.KeyEvent;

public class Player extends Entity {

    // instance vars -----------------------------------------------------------

    // stats
    private int lives = 5;

    // movement
    private boolean isThrusting = false;
    private boolean isTurningRight = false;
    private boolean isTurningLeft = false;
    private double rotationalSpeed = 10;
    private double spd = 1;

    // shooting
    private boolean shot = false;
    private double ammoStat = 10;
    private double ammo = ammoStat;
    private boolean reloading = false;

    // misc.
    private Point head;
    private double size;
    private double score = 0;
    private boolean isInvuln = false;

    // constructors ------------------------------------------------------------
    public Player() {
        super(BasePolygon.genShape(3, Constants.PLAYER_SIZE), new Vector(), 50);

        // create player polygon
        this.size = Constants.PLAYER_SIZE;
        dmg = 30;
        head = poly.verts[0];
        tag = Constants.PLAYER_TAG;
        homeChance = 0.0;
        reloadTime = 1500;
        max_speed = 2;
        projSpd = max_speed * 1.25;

        // set timers
        TIMER = new Clock.Timer();
        TIMER.addTimer(Constants.RESPAWN_KEY, 2500);
        TIMER.addTimer(Constants.INVULN_KEY, 5000);
        TIMER.addTimer(Constants.SHOOT_KEY, 150);
        TIMER.addTimer(Constants.RELOAD_KEY, reloadTime);
        TIMER.addTimer(Constants.EXHAUST_KEY, 10);
    }

    // getters -----------------------------------------------------------------

    public boolean getThrusting() {
        return isThrusting;
    }

    public double getHomeChance() {
        return homeChance;
    }

    public double getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    // setters -----------------------------------------------------------------

    public void setTimerDuration(String key, double duration) {
        TIMER.changeDuration(key, duration);
    }

    // other methods -----------------------------------------------------------

    public boolean canGenExhaust() {
        return getThrusting() && TIMER.checkTimer(Constants.EXHAUST_KEY);
    }

    // return boolean of if player can respawn
    public boolean canRespawn() {
        return TIMER.silentCheckTimer(Constants.RESPAWN_KEY) && lives > 0;
    }

    // return if player can be damaged
    public boolean canBeDamaged() {
        return !isDead() && !isInvuln;
    }

    // return true if player is reloading
    public boolean isReloading() {
        return reloading;
    }

    // check if can shoot method
    private boolean canShoot() {
        return TIMER.silentCheckTimer(Constants.SHOOT_KEY) && ammo > 0 && !reloading;
    }

    // return if can shoot homing bullet
    public boolean canShootHoming() {
        return StdRandom.uniform() < homeChance;
    }

    // check if the player has shot
    public boolean hasShot() {
        return shot;
    }

    // collide method has slight modification
    public Vector collide(Entity e) {
        if (isInvuln || isDead()) {
            return new Vector();
        }
        return poly.collide(e.poly);
    }

    // shooting method
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
        vel.subtract(Vector.scale(b.vel, b.area() / area()));
        return b;
    }

    public Bullet shootNormal() {
        Bullet b = new Bullet(head, rotation, projSpd, dmg, tag,
                              Constants.PLAYER_GLOW);
        return b;
    }

    // shoot homing method
    private Homing shootHoming() {
        Homing h = new Homing(head, rotation, projSpd, dmg, tag,
                              Constants.PLAYER_GLOW, 90);
        h.setTarget(Constants.ENEMY_TAG);
        h.setHomeWeight(20);
        return h;
    }

    // heal player
    public void heal(double value) {
        hp += value;
        hp = Math.min(hp, hpStat);
    }

    // reload method
    public void reload() {
        ammo = ammoStat;
        reloading = false;
    }

    // increase homing chance method
    public void modifyHomingChance(double scale) {
        if (scale < 0 || scale > 1) {
            throw new IllegalArgumentException("0 < modification < 1.");
        }

        if (homeChance < Constants.TOLERANCE) {
            homeChance = scale;
        }
        else {
            homeChance += (1 - homeChance) * scale;
            homeChance = Math.min(homeChance, 1);
        }
    }

    // increase dmaa
    public void modifyStrength(double scale) {
    }

    // handle movement inputs for the player
    public void handleInputs() {
        isThrusting = false;
        isTurningLeft = false;
        isTurningRight = false;
        shot = false;

        // check if player is applying thrust
        if (StdDraw.isKeyPressed(KeyEvent.VK_UP) ||
                StdDraw.isKeyPressed(KeyEvent.VK_W)) {
            isThrusting = true;
        }

        // check if the player is trying to turn
        if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT) ||
                StdDraw.isKeyPressed(KeyEvent.VK_A)) {
            isTurningLeft = true;
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT) ||
                StdDraw.isKeyPressed(KeyEvent.VK_D)) {
            isTurningRight = true;
        }

        // check if player wants to reload
        if ((StdDraw.isKeyPressed(KeyEvent.VK_DOWN) ||
                StdDraw.isKeyPressed(KeyEvent.VK_S)) && !reloading &&
                ammo < ammoStat) {
            reloading = true;
            TIMER.setCheck(Constants.RELOAD_KEY);
        }

        // check if the player is trying to shoot
        if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
            if (canShoot()) {
                shot = true;
            }
        }
    }

    // update method
    public void update(double dt) {
        // move player forward
        if (isThrusting) {
            vel.add(spd * dt, rotation);
            vel.clamp(max_speed);
        }
        else {
            vel.add(-0.05 * max_speed * dt);
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

        // check if invulnerability should go away
        if (isInvuln && TIMER.checkTimer(Constants.INVULN_KEY)) {
            isInvuln = false;
        }

        // check if ammo should be reloaded
        if (TIMER.checkTimer(Constants.RELOAD_KEY) && reloading) {
            reload();
        }
        else if (ammo == 0 && !reloading) {
            reloading = true;
            TIMER.setCheck(Constants.RELOAD_KEY);
        }

    }

    // damage method
    public void damage(double damage) {
        if (!isInvuln) {
            super.damage(damage);
        }
    }

    // die method
    public void die() {
        if (!isInvuln) {
            super.die();
            lives--;
            if (lives > 0) {
                score *= 0.75;
            }
            TIMER.setCheck(Constants.RESPAWN_KEY);
        }
    }

    // respawn method
    public void respawn() {
        hp = hpStat;
        ammo = ammoStat;
        isInvuln = true;
        TIMER.setCheck(Constants.INVULN_KEY);
    }

    // draw method
    public void draw(Vector scroll) {

        if (isInvuln) {
            double currTime = TIMER.peekTimeLeft(Constants.INVULN_KEY);
            if (currTime % 500 <= 200) {
                return;
            }
        }

        // draw the polygon
        super.draw(scroll);

        // draw line behind
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

    // increase the score of the player
    public void increaseScore(double value) {
        score += value;
    }

    // draw the number of lives left for player (right aligned)
    public void drawLives(double x, double y) {

        BasePolygon shape = BasePolygon.genShape(sides, size);
        shape.recenter(x, y);
        for (int i = 0; i < lives; i++) {
            shape.drawOutline(Constants.ACCENT_OFFSET, Constants.ACCENT_COLOR);
            shape.drawOutline(new Vector(), Constants.PRIMARY_COLOR);
            x -= size * 2;
            shape.recenter(x, y);
        }
    }

    // draw health bar (right aligned)
    public void drawHealth(double x, double y, double scale) {

        double maxWidth = scale / 2;
        double width = maxWidth * (hp / hpStat);
        double height = scale * 0.02;

        double[] px = { x, x, x - maxWidth - scale * 0.01, x - maxWidth };
        double[] py = { y - height, y, y, y - height };
        StdDraw.setPenColor(Constants.ACCENT_COLOR);
        StdDraw.polygon(px, py);

        for (int i = 0; i < px.length; i++) {
            px[i] += Constants.ACCENT_OFFSET.x;
            py[i] += Constants.ACCENT_OFFSET.y;
        }

        px[2] += maxWidth - width;
        px[3] += maxWidth - width;

        Color hpGlow;
        if (hp > hpStat * 0.5) {
            double rate = (hpStat - hp) / hpStat;
            hpGlow = VFX.lerp(Constants.PLAYER_GLOW, Constants.P_MID_HP, rate);
        }
        else {
            double rate = (hpStat - hp) / hpStat;
            hpGlow = VFX.lerp(Constants.P_MID_HP, Constants.P_LOW_HP, rate);
        }

        VFX.glow(px, py, hpGlow, 1);

        StdDraw.setPenColor(Constants.PRIMARY_COLOR);
        StdDraw.polygon(px, py);
    }

    // draw ammo (right aligned)
    public void drawAmmo(double x, double y, double scale) {
        double width = Bullet.BASE_SIZE * 2;
        double offX = Constants.ACCENT_OFFSET.x;
        double offY = Constants.ACCENT_OFFSET.y;

        double bx = x;

        for (int i = 0; i < ammo; i++) {
            StdDraw.setPenColor(Constants.ACCENT_COLOR);
            StdDraw.circle(bx - offX, y - offY, Bullet.BASE_SIZE / 2);
            StdDraw.setPenColor(Constants.PRIMARY_COLOR);
            StdDraw.circle(bx, y, Bullet.BASE_SIZE / 2);
            bx -= width;
        }

        // draw reload progress
        if (reloading) {
            double maxWidth = Bullet.BASE_SIZE * 2 * (ammoStat - 1);
            double height = scale * 0.01;

            double duration = TIMER.peekTimeLeft(Constants.RELOAD_KEY);

            y -= scale * 0.03;

            double[] px = { x, x, x - maxWidth - scale * 0.01, x - maxWidth };
            double[] py = { y - height, y, y, y - height };
            StdDraw.setPenColor(Constants.ACCENT_COLOR);
            StdDraw.polygon(px, py);

            for (int i = 0; i < px.length; i++) {
                px[i] += offX;
                py[i] += offY;
            }

            double v1 = maxWidth * (duration / reloadTime);
            px[2] = x - v1 - scale * 0.01;
            px[3] = x - v1;
            StdDraw.setPenColor(Constants.PRIMARY_COLOR);
            StdDraw.polygon(px, py);
        }
    }

    public String toString() {
        return String.format("HP: %.2f\nDMG: %.2f\nH: %.2f", hpStat, dmg, homeChance);
    }

    public static void main(String[] args) {
    }
}
