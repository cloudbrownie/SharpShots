import java.util.ArrayList;

public class Entity {

    // static methods ----------------------------------------------------------

    // resolve a collision between two entities, e1 is assumed as aggressor
    public static void resolveCollision(Entity e1, Entity e2, Vector mtv) {
        e1.translate(Vector.scale(mtv, 0.5));
        e2.translate(Vector.scale(mtv, -0.5));

        Vector e1Vel = new Vector();
        Vector e2Vel = new Vector();
        // calculate new velocities (assume perfect elastic collisions)
        double m1 = e1.area();
        double m2 = e2.area();
        double denom = m1 + m2;

        e1Vel.x = ((m1 - m2) / denom) * e1.vel.x + (2 * m2 / denom) * e2.vel.x;
        e1Vel.y = ((m1 - m2) / denom) * e1.vel.y + (2 * m2 / denom) * e2.vel.y;

        e2Vel.x = (2 * m1 / denom) * e1.vel.x + ((m2 - m1) / denom) * e2.vel.x;
        e2Vel.y = (2 * m1 / denom) * e1.vel.y + ((m2 - m1) / denom) * e2.vel.y;

        e1.vel = e1Vel;
        e2.vel = e2Vel;
    }

    // instance vars -----------------------------------------------------------

    // physical properties
    protected CollidablePolygon poly;
    protected Vector vel;
    protected int sides;
    protected double hpStat;
    protected double hp;
    protected double dmg;
    protected double homeChance = 0;
    protected double rotation = 0; // degrees
    protected double angularVel = 0;
    protected double dropChance;
    protected double reloadTime;
    protected double spd;
    protected double max_speed;
    protected double projSpd;

    // differentiating entities
    protected char tag;

    // head pointer
    protected Point head;

    // points for killing this entity
    protected double points;

    protected ArrayList<Buff> buffs = new ArrayList<>();
    protected Clock.Timer TIMER;

    // constructors ------------------------------------------------------------

    // default constructor
    public Entity() {
    }

    // constructor using a base polygon
    public Entity(BasePolygon b, Vector vel, double hp) {
        this.vel = new Vector(vel);
        this.hp = hp;
        poly = new CollidablePolygon(b);
        _init();
    }

    // constructor with values
    public Entity(double[] x, double[] y, Vector vel) {
        this.vel = new Vector(vel);
        poly = new CollidablePolygon(x, y);
        hp = poly.area();
        _init();
    }

    // constructor using a base polygon
    public Entity(BasePolygon b, Vector vel) {
        this.vel = new Vector(vel);
        poly = new CollidablePolygon(b);
        hp = poly.area();
        _init();
    }

    // not a constructor but avoids repetition
    private void _init() {
        sides = poly.getN();
        rotation = 0;
        hpStat = hp;
        head = poly.verts[0];
        dropChance = 0;
        points = 0;
    }

    // getters -----------------------------------------------------------------

    public char getTag() {
        return tag;
    }

    public double getHpStat() {
        return hpStat;
    }

    public double getHp() {
        return hp;
    }

    public double getAngularVel() {
        return angularVel;
    }

    public double getPoints() {
        return points;
    }

    public double getHomeChance() {
        return homeChance;
    }

    public double getRadius() {
        return poly.getRadius();
    }

    public CollidablePolygon getPoly() {
        return new CollidablePolygon(poly);
    }

    public Vector getVel() {
        return new Vector(vel);
    }

    public Point getHead() {
        return new Point(head);
    }

    // setters -----------------------------------------------------------------

    public void setPoly(CollidablePolygon poly) {
        this.poly = new CollidablePolygon(poly);
    }

    public void setVel(Vector vel) {
        this.vel = new Vector(vel);
    }

    public void setHpStat(double hpStat) {
        this.hpStat = hpStat;
    }

    public void setHp(double hp) {
        this.hp = hp;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public void setAngularVel(double angularVel) {
        this.angularVel = angularVel;
    }

    public void setTag(char tag) {
        this.tag = tag;
    }

    public void setHead(Point head) {
        this.head = new Point(head);
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public void setHomeChance(double homeChance) {
        this.homeChance = homeChance;
    }

    public void setPosition(Point p) {
        poly.recenter(p);
    }

    public void setTimerDuration(String key, double duration) {
        TIMER.changeDuration(key, duration);
    }

    // other methods -----------------------------------------------------------

    // return if the entity is dead (hp == 0)
    public boolean isDead() {
        return hp < Constants.TOLERANCE;
    }

    // return if then entity can be damaged
    public boolean canBeDamaged() {
        return !isDead();
    }

    // return if entity can drop pickup items
    protected boolean canDropBuffs() {
        return StdRandom.uniform() < dropChance;
    }

    // return the rotation of this entity, degrees
    public double getRotation() {
        return rotation;
    }

    // return the area of this entity
    public double area() {
        return poly.area();
    }

    // get the center of the entity
    public Point center() {
        return poly.center();
    }

    // return the minimum translation vector for this collision
    public Vector collide(Entity e) {
        return poly.collide(e.poly);
    }

    // drop all buffs
    public ArrayList<Buff> dropBuffs() {
        Point c = center();
        buffs.forEach(b -> b.setPosition(c));
        return buffs;
    }

    // add a buff to the entity
    public void addBuff(Buff d) {
        buffs.add(d);
        d.apply(this);
    }

    // generate buffs
    protected void genBuffs(int drops) {
        if (canDropBuffs()) {
            Point c = center();

            for (int i = 0; i < drops; i++) {
                addBuff(Buff.genBuff(c.x, c.y));
            }
        }
    }

    // modify vel
    public void modifyVel(Vector v) {
        vel.add(v);
    }

    // translate method
    public void translate(Vector v) {
        poly.translate(v);
    }

    // translate method with x and y
    public void translate(double dx, double dy) {
        poly.translate(dx, dy);
    }

    // move method
    public void move(double dt) {
        translate(Vector.scale(vel, dt));
    }

    // rotate the entity
    public void rotate(double degrees) {
        rotation = (rotation + degrees) % 360;
        poly.rotate(degrees);
    }

    // update method (updates state and position)
    public void update(double dt) {
        move(dt);
        rotate(angularVel * dt);
        vel.setOrigin(poly.center());
    }

    // kill the entity
    public void die() {
        hp = 0;
    }

    // damage this entity
    public void damage(double damage) {
        hp -= damage;
        if (isDead()) {
            die();
        }
    }

    public ArrayList<VFX.Effect> genBuffParticles() {
        ArrayList<VFX.Effect> particles = new ArrayList<>();
        double chance = Constants.BASE_BUFF_PARTICLE_CHANCE / buffs.size();
        for (Buff b : buffs) {
            if (StdRandom.uniform() < chance) {
                double mag = StdRandom.uniform() * 1 - 0.5;
                double degrees = StdRandom.uniform() * 360;
                Vector vel = Vector.genVector(mag, degrees);
                double size = StdRandom.uniform() * 0.5 + 0.5;
                particles.add(new VFX.Glow(center(), vel, size, b.glow));
            }
        }
        return particles;
    }

    // draw method
    public void draw(Vector scroll) {
        poly.drawOutline(scroll, Constants.PRIMARY_COLOR);
    }

    // draw the offset stuff
    public void drawUnderline(Vector scroll) {
        poly.drawOutline(Vector.add(scroll, Constants.ACCENT_OFFSET),
                         Constants.ACCENT_COLOR);
    }

    // debug drawing method
    public void drawDebug(Vector scroll) {
        poly.drawDebug(scroll);
    }

    public static void main(String[] args) {
    }
}
