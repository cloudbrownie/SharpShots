import java.util.ArrayList;

public class Asteroid extends Entity {

    // static vars -------------------------------------------------------------

    // maximum number of vertices for an asteroid
    public static final int MAX_VERTICES = 15;

    // size threshold needed to generate more asteroids (kinda arbitrary)
    public static final double PARENT_SIZE_THRESHOLD = 5;

    // static methods ----------------------------------------------------------

    // return an asteroid given a center position and a velocity
    public static Asteroid genAsteroid(double x, double y, Vector vel,
                                       double rad) {
        // create a random number of vertices
        int n = (int) StdRandom.uniform() * MAX_VERTICES + 6;
        n = Math.min(n, MAX_VERTICES);

        double[] px = new double[n];
        double[] py = new double[n];

        // generate the first point
        px[0] = x + rad;
        py[0] = y;
        // degrees for moving around the origin
        int degreeIncr = 360 / n;
        int currDegree = degreeIncr;

        // create other points
        for (int i = 1; i < n; i++) {
            // find the new distance outwards
            double dist = StdRandom.uniform() * rad / 2 + rad / 2;

            // find the x and y components
            px[i] = dist * Math.cos(Math.toRadians(currDegree)) + x;
            py[i] = dist * Math.sin(Math.toRadians(currDegree)) + y;

            // increase degrees
            currDegree += degreeIncr;
        }

        // create the asteroid
        Asteroid a = new Asteroid(px, py, vel, rad);

        // rotate the asteroid by some random degrees
        a.poly.rotate(StdRandom.uniform(360));

        return a;
    }

    // return a random asteroid using just a position
    public static Asteroid genRandomAsteroid(Point p, double scale) {

        // create some random velocity
        Vector vel = new Vector(StdRandom.uniform() - 0.5,
                                StdRandom.uniform() - 0.5);

        double length = StdRandom.uniform() * scale * 0.03 + scale * 0.03;

        return genAsteroid(p.x, p.y, vel, length);
    }

    public static ArrayList<Asteroid> genChildren(Asteroid a) {
        ArrayList<Asteroid> children = new ArrayList<>();

        double size = a.getRadius() * 0.9;
        double cr = size * StdRandom.uniform(0.4, 0.5);
        while (cr > 0.5) {
            // generate values for new child
            Vector vel = new Vector(a.vel);
            vel.rotate(StdRandom.uniform() * 360);
            vel.scale(StdRandom.uniform() * 0.5 + 1.5);

            Point c = a.center();
            c.x += StdRandom.uniform() * cr * 0.3 - cr * 0.7;
            c.y += StdRandom.uniform() * cr * 0.3 - cr * 0.7;
            Asteroid na = Asteroid.genAsteroid(c.x, c.y, vel, cr);
            children.add(na);
            size -= cr;
            cr = size * StdRandom.uniform(0.3, 0.4);
        }

        return children;
    }

    // instance vars -----------------------------------------------------------

    // store max side radius
    private double radius;
    private boolean isLucky;

    // constructors ------------------------------------------------------------

    // asteroid constructor with parallel arrays
    public Asteroid(double[] x, double[] y, Vector vel, double radius) {
        super(x, y, vel);
        this.radius = radius;

        hpStat = area() * 2;
        hp = hpStat;
        tag = Constants.ASTEROID_TAG;
        points = area() / 2;
        angularVel = StdRandom.uniform() * 10 - 5;
        isLucky = StdRandom.uniform() < 0.01;
        if (isLucky) {
            points = 100;
        }
    }

    // getters -----------------------------------------------------------------

    // return radius
    public double getRadius() {
        return radius;
    }

    // other methods -----------------------------------------------------------

    // check if the size of the asteroid is big enough to spawn more asteroids
    public boolean canSpawn() {
        return poly.area() >= PARENT_SIZE_THRESHOLD;
    }

    // area method
    public double area() {
        return poly.area();
    }

    public void draw(Vector scroll) {
        super.draw(scroll);

        if (isLucky) {
            double[] x = poly.xVerts(scroll.x);
            double[] y = poly.yVerts(scroll.y);
            VFX.glow(x, y, Constants.LUCKY_GLOW, 1);
        }
    }

    public static void main(String[] args) {
    }
}
