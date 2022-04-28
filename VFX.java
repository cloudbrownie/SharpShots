import java.awt.Color;
import java.util.ArrayList;

public class VFX {

    // subclasses --------------------------------------------------------------

    // effect subclass
    public class Effect {

        // life boolean
        protected boolean dead = false;

        // default constructor
        public Effect() {

        }

        // draw method
        public void draw(Vector scroll) {

        }

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

    }

    // circular glow particle subclass
    public class GlowParticle extends Effect {

        // glow color
        private Color glow;

        // position as x and y (point obj is unnecessary)
        private double x;
        private double y;

        // store a point in case the glow particle needs to follow a particle
        private Point follow;

        // glow radius
        private double radius;
        private double innerRadius;

        // glow intensity
        private double intensity;

        // constructor
        public GlowParticle(double x, double y, double radius, Color glow) {
            this.glow = glow;
            this.x = x;
            this.y = y;
            this.radius = radius * 3;
            innerRadius = radius;
            intensity = 1;
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

        // set the point to follow method
        public void followPoint(Point p) {
            follow = p;
        }

        // set the intensity for the glow effect
        public void setIntensity(double intensity) {
            if (intensity <= 0 || intensity > 1) {
                throw new IllegalArgumentException("0.0 < Clarity < 1.0");
            }
            this.intensity = intensity;
        }

        // set the inner radius for the glow intensity to stop at
        public void setInnerRadius(double innerRadius) {
            this.innerRadius = innerRadius;
        }

        // draw method
        public void draw(Vector scroll) {

            Color save = StdDraw.getPenColor();
            StdDraw.setPenColor(glow);

            // calculate intensity steps
            double area = radius - innerRadius;
            double radStep = area * (1.1 - intensity);
            double xpos = x;
            double ypos = y;
            if (follow != null) {
                xpos = follow.x;
                ypos = follow.y;
            }

            // iterate for each step
            for (double currRad = innerRadius; currRad < radius;
                 currRad += radStep) {
                // draw a glow circle
                StdDraw.filledCircle(xpos, ypos, currRad);
            }


            StdDraw.setPenColor(save);
        }
    }

    // spark particle subclass
    public class Spark extends Effect {

    }

    // pulse subclass
    public class Pulse extends Effect {

    }

    // static methods ----------------------------------------------------------

    // render a glow effect
    public static void glow(double x, double y, double radius, Color glow,
                            double intensity) {
        if (intensity <= 0.1 || intensity > 1) {
            throw new IllegalArgumentException("0.1 <= Intensity < 1.0");
        }

        Color save = StdDraw.getPenColor();
        StdDraw.setPenColor(glow);

        double radiusStep = radius * (1.1 - intensity);

        // iterate for each radius step
        for (double currRadius = 0; currRadius < radius - radiusStep;
             currRadius += radiusStep) {
            // draw the glow
            StdDraw.filledCircle(x, y, currRadius);
        }

        StdDraw.setPenColor(save);
    }


    // actual class stuff ------------------------------------------------------

    // store all effects in a single list
    ArrayList<Effect> effects;

    // constructor
    public VFX() {
        effects = new ArrayList<>();
    }

    // add a glow particle
    public void addGlow(Point p, double radius, Color glow, boolean follow) {
        GlowParticle g = new GlowParticle(p.x, p.y, radius, glow);
        g.setIntensity(1);
        if (follow) {
            g.followPoint(p);
        }
        effects.add(g);
    }

    // update effects method
    public void update(Vector scroll, double dt) {
        for (Effect effect : effects) {
            effect.update(dt);
            effect.draw(scroll);
        }

        effects.removeIf(Effect::isDead);
    }

    public static void main(String[] args) {

    }
}
