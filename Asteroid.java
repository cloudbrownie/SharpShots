import java.util.ArrayList;

public class Asteroid extends Entity {

    // maximum number of vertices for an asteroid
    public static final int MAX_VERTICES = 15;

    // size threshold needed to generate more asteroids (kinda arbitrary)
    public static final double PARENT_SIZE_THRESHOLD = 35;

    // spawn angle in degrees
    public static final int SPAWN_ANGLE = 45;

    // store max side radius
    private double radius;

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

    // return a random asteroid (used in main method for testing purposes)
    public static Asteroid genRandomAsteroid(double scale) {

        // generate the center of the asteroid
        double x = StdRandom.uniform() * scale;
        double y = StdRandom.uniform() * scale;

        // create some random velocity
        Vector vel = new Vector(StdRandom.uniform() - 0.5, StdRandom.uniform() - 0.5);

        double length = StdRandom.uniform() * scale * 0.03 + scale * 0.03;

        return genAsteroid(x, y, vel, length);
    }

    // generate a random spawn angle
    public static double genSpawnAngle() {
        return StdRandom.uniform(SPAWN_ANGLE) - SPAWN_ANGLE / 2;
    }

    // asteroid constructor with point array
    public Asteroid(Point[] points, Vector vel, double radius) {
        poly = new CollidablePolygon(points);
        this.vel = vel;
        hp = area() * 3;
        tag = 'a';
        this.radius = radius;
    }

    // asteroid constructor with parallel arrays
    public Asteroid(double[] x, double[] y, Vector vel, double radius) {
        poly = new CollidablePolygon(x, y);
        this.vel = vel;
        hp = area() * 3;
        tag = 'a';
        this.radius = radius;
    }

    // return radius
    public double getRadius() {
        return radius;
    }

    // add velocity to this object
    public void addVelocity(Vector v) {
        vel.add(v);
    }

    // change the velocity
    public void setVelocity(Vector v) {
        vel.copy(v);
    }

    // check if the size of the asteroid is big enough to spawn more asteroids
    public boolean canSpawn() {
        return poly.area() >= PARENT_SIZE_THRESHOLD;
    }

    // return the center of the asteroid
    public Point centroid() {
        return poly.centroid();
    }

    // area method
    public double area() {
        return poly.area();
    }

    // debug drawing method
    public void drawDebug(Vector scroll) {
        poly.drawDebug(scroll);
        vel.drawDebug(scroll);
    }

    // draw the velocity vector of the asteroid
    public void drawVel(Vector scroll) {
        Vector scaled = Vector.scale(vel, 5);
        scaled.drawDebug(scroll);
    }

    public static void main(String[] args) {

        double scale = 100;
        StdDraw.enableDoubleBuffering();
        StdDraw.setScale(0, scale);

        ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
        double length = scale * 0.05;
        asteroids.add(genAsteroid(scale * 0.5, scale, new Vector(0, -0.5), length));
        asteroids.add(genAsteroid(0, scale * 0.5, new Vector(0.5, 0), length));
        asteroids.add(genAsteroid(scale, scale * 0.5, new Vector(-0.5, 0), length));
        asteroids.add(genAsteroid(scale * 0.5, 0, new Vector(0, 0.5), length));

        Player p = new Player(50, 50, 5);

        Clock clock = new Clock();

        Vector scroll = new Vector();

        clock.start();
        while (true) {

            double dt = clock.tick();

            StdDraw.clear();

            // reset if spacebar hit
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == ' ') {
                    asteroids.clear();
                    asteroids.add(genAsteroid(scale * 0.5, scale,
                                              new Vector(0, -0.5), length));
                    asteroids.add(genAsteroid(0, scale * 0.5,
                                              new Vector(0.5, 0), length));
                    asteroids.add(genAsteroid(scale, scale * 0.5,
                                              new Vector(-0.5, 0), length));
                    asteroids.add(genAsteroid(scale * 0.5, 0,
                                              new Vector(0, 0.5), length));
                }
                else if (key == 'o') {
                    asteroids.clear();
                    asteroids.add(genAsteroid(scale * 0.5, scale * 0.5,
                                              new Vector(0, 0), length));
                    asteroids.add(genAsteroid(scale * 0.5, scale * 0.5,
                                              new Vector(0, 0), length));
                }
            }

            for (Asteroid a : asteroids) {
                a.update(dt);
            }

            p.update(dt);

            p.handleInputs();

            for (int i = 0; i < asteroids.size() - 1; i++) {
                Asteroid a = asteroids.get(i);

                for (int j = i + 1; j < asteroids.size(); j++) {
                    Asteroid b = asteroids.get(j);
                    Vector mtv = a.collide(b);
                    if (mtv.isNonZero()) {
                        Entity.resolveCollision(a, b, mtv);
                    }

                    Vector pmtv = a.collide(p);
                    if (pmtv.isNonZero()) {
                        p.die();
                    }
                }
            }


            // update the asteroids
            for (Asteroid asteroid : asteroids) {
                asteroid.draw(scroll);
            }

            p.draw(scroll);

            // kill asteroids that are too far off screen

            StdDraw.show();
            StdDraw.pause(20);
        }

    }
}
