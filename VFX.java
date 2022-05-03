import java.awt.Color;
import java.util.ArrayList;

public class VFX {

    // static vars -------------------------------------------------------------

    private static final double EXHAUST_RAD = Constants.SCALE * 0.005;
    private static final double EXHAUST_DEAD_RAD = EXHAUST_RAD * 0.3;
    private static final double SPARK_DEAD_LEN = Constants.SCALE * 0.01;
    private static final double SPARK_MAX_LEN = Constants.SCALE * 0.05;
    private static final double PULSE_DEAD_TOLERANCE = Constants.SCALE * 0.005;

    private static final Color[] EXHAUST_COLORS = {
            new Color(255, 50, 50, 10), new Color(255, 69, 0, 10),
            new Color(255, 191, 0, 10), new Color(255, 168, 18, 10)
    };

    // static methods ----------------------------------------------------------

    private static void throwIntensityError(double intensity) {
        if (intensity < 0.1 || intensity > 1) {
            throw new IllegalArgumentException("0.1 <= Intensity <= 1");
        }

    }

    // render a glow effect
    public static void glow(Point p, double rad, Color glow, double intensity) {
        throwIntensityError(intensity);
        StdDraw.setPenColor(glow);

        double basePenSize = StdDraw.getPenRadius();
        double penSize = basePenSize;

        int iterations = (int) (10 * intensity);

        for (int i = 0; i < iterations; i++) {
            StdDraw.circle(p.x, p.y, rad);

            penSize *= 1.5;
            StdDraw.setPenRadius(penSize);
        }

        StdDraw.setPenRadius(basePenSize);
    }

    // render a glowing polygon
    public static void glow(double[] x, double[] y, Color glow,
                            double intensity) {
        throwIntensityError(intensity);
        StdDraw.setPenColor(glow);

        double basePenSize = StdDraw.getPenRadius();
        double penSize = basePenSize;

        int iterations = (int) (10 * intensity);

        for (int i = 0; i < iterations; i++) {
            StdDraw.polygon(x, y);

            penSize *= 1.5;
            StdDraw.setPenRadius(penSize);
        }

        StdDraw.setPenRadius(basePenSize);
    }

    public static Spark genExhaustSpark(Point p, Vector vel) {
        Vector newVel = new Vector(vel);
        newVel.invert();
        p.add(newVel);
        newVel.scale(StdRandom.uniform() * 0.5 + 0.25);
        newVel.rotate(StdRandom.uniform() * 90 - 45);
        return new Spark(p, newVel);
    }

    public static Color lerp(Color c1, Color c2, double value) {
        double r1 = c1.getRed();
        double g1 = c1.getGreen();
        double b1 = c1.getBlue();
        double a1 = c1.getAlpha();
        double r2 = c2.getRed();
        double g2 = c2.getGreen();
        double b2 = c2.getBlue();
        double a2 = c2.getAlpha();
        int r = clampRGB((int) (r1 + ((r2 - r1) * value)));
        int g = clampRGB((int) (g1 + ((g2 - g1) * value)));
        int b = clampRGB((int) (b1 + ((b2 - b1) * value)));
        int a = clampRGB((int) (a1 + ((a2 - a1) * value)));
        return new Color(r, g, b, a);
    }

    private static int clampRGB(int val) {
        val = Math.min(255, val);
        val = Math.max(0, val);
        return val;
    }

    // subclasses --------------------------------------------------------------

    // effect subclass
    public static class Effect {

        // instance vars -------------------------------------------------------

        // life boolean
        protected boolean dead = false;

        // constructors --------------------------------------------------------

        // default constructor
        public Effect() {

        }

        // other methods -------------------------------------------------------

        // update method
        public void update(double dt) {

        }

        // die method
        public void die() {
            dead = true;
        }

        // return dead boolean
        public boolean isDead() {
            return dead;
        }

        // draw method
        public void draw(Vector scroll) {

        }
    }

    // circular glow particle subclass
    public static class Glow extends Effect {

        // exhaust attrs
        private Color glow;
        private Color glowLerp;
        private Point p;
        private Vector vel;
        private double radius;
        private double lerpRate = 5;

        // constructor
        public Glow(Point p, Vector vel, double radius, Color glow) {
            this.glow = glow;
            this.p = new Point(p);
            this.vel = new Vector(vel);
            this.radius = radius;
        }

        public Glow(Point p, Vector vel, double radius, Color g1, Color g2) {
            this.p = new Point(p);
            this.vel = new Vector(vel);
            this.radius = radius;
            glow = g1;
            glowLerp = g2;
        }

        // reset the alpha value of the glow
        public void setAlpha(double alpha) {
            if (alpha < 0 || alpha > 1) {
                throw new IllegalArgumentException("0.0 <= alpha <= 1.0");
            }
            int r = glow.getRed();
            int g = glow.getGreen();
            int b = glow.getBlue();
            glow = new Color(r, g, b, (int) (255 * alpha));
        }

        // get the glow color
        public Color getColor() {
            return glow;
        }

        // update method
        public void update(double dt) {
            p.add(vel);

            radius -= radius * 0.075 * dt;
            if (radius < EXHAUST_DEAD_RAD) {
                die();
            }

            if (glowLerp != null) {
                glow = lerp(glow, glowLerp, dt / lerpRate);
            }
        }

        // draw method
        public void draw(Vector scroll) {
            StdDraw.setPenColor(Constants.PRIMARY_COLOR);
            StdDraw.circle(p.x - scroll.x, p.y - scroll.y, radius);

            Point c = new Point(p);
            c.subtract(scroll);
            glow(c, radius * 3, glow, 1);
        }
    }

    // spark particle subclass
    public static class Spark extends Effect {

        // spark attrs
        private Point p;
        private Vector vel;

        public Spark(Point p, Vector vel) {
            this.p = new Point(p);
            this.vel = new Vector(vel);
        }

        public void update(double dt) {
            p.add(vel);
            vel.subtract(vel.norm() * 0.1 * dt);
            if (vel.norm() <= SPARK_DEAD_LEN) {
                die();
            }
        }

        public void draw(Vector scroll) {
            double mag = vel.norm();
            double angle = vel.rotAngle();
            double hPI = Math.PI / 2;

            double[] px = {
                    p.x + mag * Math.cos(angle) - scroll.x,
                    p.x + mag * Math.cos(angle + hPI) * 0.25 - scroll.x,
                    p.x - mag * Math.cos(angle) * 1.5 - scroll.x,
                    p.x + mag * Math.cos(angle - hPI) * 0.25 - scroll.x
            };
            double[] py = {
                    p.y + mag * Math.sin(angle) - scroll.y,
                    p.y + mag * Math.sin(angle + hPI) * 0.25 - scroll.y,
                    p.y - mag * Math.sin(angle) * 1.5 - scroll.y,
                    p.y - mag * Math.sin(angle + hPI) * 0.25 - scroll.y
            };

            StdDraw.setPenColor(Constants.PRIMARY_COLOR);
            StdDraw.polygon(px, py);
        }

    }

    // pulse subclass
    public static class Pulse extends Effect {

        // pulse attrs
        private Point p;
        private double r2;
        private double r1;
        private double maxR;
        private Color glow;

        public Pulse(Point p, double radius) {
            this.p = new Point(p);
            maxR = radius;
            r2 = maxR / 2;
            r1 = 0;
        }

        public Pulse(Point p, double radius, Color glow) {
            this.p = new Point(p);
            this.glow = glow;
            maxR = radius;
            r2 = maxR / 2;
            r1 = 0;
        }

        public void update(double dt) {
            r2 += (maxR - r2) * 0.5 * dt;
            r2 = Math.min(r2, maxR);
            r1 += (maxR - r1) * 0.4 * dt;
            if (r2 - r1 <= PULSE_DEAD_TOLERANCE) {
                die();
            }
        }

        public void draw(Vector scroll) {
            StdDraw.setPenColor(Constants.PRIMARY_COLOR);
            StdDraw.circle(p.x - scroll.x, p.y - scroll.y, r1);
            StdDraw.circle(p.x - scroll.x, p.y - scroll.y, r2);
            if (glow != null) {
                glow(new Point(p.x - scroll.x, p.y - scroll.y), r2, glow, 1);
            }
        }
    }

    // actual class stuff ------------------------------------------------------

    // store all effects in a single list
    private ArrayList<Effect> effects;

    // constructor
    public VFX() {
        effects = new ArrayList<>();
    }

    // restart method
    public void clear() {
        effects.clear();
    }

    // add effects
    public void addEffects(ArrayList<Effect> effects) {
        this.effects.addAll(effects);
    }

    // add a glow particle
    public void addGlow(Point p, Vector vel, double radius, Color glow) {
        Glow e = new Glow(p, vel, radius, glow);
        effects.add(e);
    }

    // special glow particle
    public void addExhaust(Point p, Vector vel) {
        int i = StdRandom.uniform(EXHAUST_COLORS.length);
        Vector newVel = new Vector(vel);
        newVel.invert();
        p.add(newVel);
        newVel.scale(StdRandom.uniform() * 0.25 + 0.25);
        newVel.rotate(StdRandom.uniform() * 45 - 22.5);
        double radius = StdRandom.uniform() * EXHAUST_RAD / 2 + EXHAUST_RAD / 2;
        effects.add(new Glow(p, newVel, radius, EXHAUST_COLORS[i],
                             new Color(0, 0, 0, 10)));
    }

    // add a spark particle
    public void addSparks(int n, Point p, Vector vel, double angleRange) {
        for (int i = 0; i < n; i++) {
            Vector v = new Vector(vel);
            v.rotate(StdRandom.uniform() * angleRange - angleRange / 2);
            v.scale(StdRandom.uniform(0.5, 2));
            v.clamp(SPARK_MAX_LEN);
            effects.add(new Spark(p, v));
        }
    }

    public void addPulse(Point p, double radius) {
        effects.add(new Pulse(p, radius));
    }

    public void addPulse(Point p, double radius, Color glow) {
        effects.add(new Pulse(p, radius, glow));
    }

    public void genExplosion(Point p, double size) {
        Vector vSize = new Vector(size, 0);
        vSize.clamp(1.5);
        addSparks(StdRandom.uniform(5, 8), p, vSize, 360);
        addPulse(p, size);
    }

    // update effects method
    public void update(Vector scroll, double dt) {
        effects.forEach(e -> {
            e.update(dt);
            e.draw(scroll);
        });

        effects.removeIf(Effect::isDead);
    }

    public static void main(String[] args) {

    }
}
