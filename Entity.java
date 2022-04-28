public class Entity {

    // tolerance for comparing values
    private static double TOLERANCE = 1.0e-3;

    // collision polygon for entity
    protected CollidablePolygon poly;

    // velocity for entity
    protected Vector vel;

    // health for the entity
    protected double hp;

    // rotational angle for the entity (degrees)
    protected double rotation;

    // angular velocity
    protected double angularVel;

    // character tag used for differentiating which bullets damage what
    protected char tag;

    // default constructor
    public Entity() {
    }

    // constructor with values
    public Entity(Point[] verts, Vector vel, double hp) {
        poly = new CollidablePolygon(verts);
        this.vel = vel;
        this.hp = hp;
        rotation = 0;
        angularVel = 0;
    }

    // constructor with values
    public Entity(double[] x, double[] y, Vector vel, double hp) {
        poly = new CollidablePolygon(x, y);
        this.vel = vel;
        this.hp = hp;
        rotation = 0;
        angularVel = 0;
    }

    // constructor using a base polygon
    public Entity(BasePolygon b, Vector vel, double hp) {
        poly = new CollidablePolygon(b);
        this.vel = vel;
        this.hp = hp;
        rotation = 0;
        angularVel = 0;
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

    // set the velocity of this entity
    public void setVelocity(Vector v) {
        vel.copy(v);
    }

    // return tag
    public char getTag() {
        return tag;
    }

    // rotate the entity
    public void rotate(double degrees) {
        rotation += degrees;
        poly.rotate(degrees);
    }

    // return the rotation of this entity, degrees
    public double getRotation() {
        return rotation;
    }

    // return the area of this entity
    public double area() {
        return poly.area();
    }

    // return the minimum translation vector for this collision
    public Vector collide(Entity e) {
        return poly.collide(e.poly);
    }

    // update method (updates state and position)
    public void update(double dt) {
        move(dt);
        rotate(angularVel * dt);
        vel.setOrigin(poly.centroid());
    }

    // return if the entity is dead (hp == 0)
    public boolean isDead() {
        return hp < TOLERANCE;
    }

    // kill the entity
    public void die() {
        hp = 0;
    }

    // damage this entity
    public void damage(double damage) {
        hp -= damage;
        hp = Math.max(hp, 0); // clamp hp to 0
    }

    // draw method
    public void draw(Vector scroll) {
        poly.draw(scroll);
    }

    // resolve a collision between two entities, e1 is assumed as aggressor
    public static void resolveCollision(Entity e1, Entity e2, Vector mtv) {
        e1.translate(Vector.scale(mtv, 0.5));
        e2.translate(Vector.scale(mtv, -0.5));

        Vector thisVel = new Vector();
        Vector thatVel = new Vector();
        // calculate new velocities (assume perfect elastic collisions)
        double m1 = e1.area();
        double m2 = e2.area();
        double denom = m1 + m2;

        thisVel.x = ((m1 - m2) / denom) * e1.vel.x + (2 * m2 / denom) * e2.vel.x;
        thisVel.y = ((m1 - m2) / denom) * e1.vel.y + (2 * m2 / denom) * e2.vel.y;

        thatVel.x = (2 * m1 / denom) * e1.vel.x + ((m2 - m1) / denom) * e2.vel.x;
        thatVel.y = (2 * m1 / denom) * e1.vel.y + ((m2 - m1) / denom) * e2.vel.y;

        e1.setVelocity(thisVel);
        e2.setVelocity(thatVel);
    }

    public static void main(String[] args) {

        double scale = 100;
        StdDraw.setScale(0, 100);
        StdDraw.enableDoubleBuffering();

        Entity e = new Entity(BasePolygon.generateShape(5, 10), new Vector(0.1, 0.1), 0);
        e.translate(10, 10);

        Clock clock = new Clock();

        Vector scroll = new Vector();

        clock.start();
        while (true) {

            double dt = clock.tick();

            StdDraw.clear();

            e.update(dt);

            e.draw(scroll);

            StdDraw.show();
        }
    }
}
