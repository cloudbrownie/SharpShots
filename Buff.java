import java.awt.Color;

public class Buff extends Entity {

    // static vars -------------------------------------------------------------

    private static final double INCR_SCALE = 40.0;
    private static final double HOMING_RAD = Constants.SCALE * 0.09;
    private static final double DAMAGE_RAD = Constants.SCALE * 0.08;
    private static final double HEALTH_RAD = Constants.SCALE * 0.05;
    private static final double SPEED_RAD = Constants.SCALE * 0.06;
    private static final double RELOAD_RAD = Constants.SCALE * 0.07;

    // static methods ----------------------------------------------------------

    public static Buff genBuff(double x, double y) {
        // probabilities in order of: homing, damage, speed, health
        int[] freqs = { 1, 3, 5, 5, 4 };

        switch (StdRandom.discrete(freqs)) {
            case 0:
                return new HomingPowerup(x, y, Constants.DROP_SIZE);
            case 1:
                return new DamageUp(x, y, Constants.DROP_SIZE);
            case 2:
                return new HealthUp(x, y, Constants.DROP_SIZE);
            case 3:
                return new SpeedUp(x, y, Constants.DROP_SIZE);
            case 4:
                return new ReloadBoost(x, y, Constants.DROP_SIZE);
            default:
                return new Buff();
        }
    }

    // instance vars -----------------------------------------------------------

    protected double size;
    protected double buffRad;
    protected int value = 1;
    protected Color glow;

    // constructors ------------------------------------------------------------

    // default constructor
    public Buff() {
    }

    // constructor
    public Buff(double x, double y, int sides, double size, char tag,
                double rad) {
        super(BasePolygon.genShape(sides, size), new Vector());
        this.size = size;
        this.tag = tag;
        buffRad = rad;
        poly.recenter(x, y);
    }

    // other methods -----------------------------------------------------------

    public void apply(Entity e) {
    }

    public void draw(Vector scroll) {
        super.draw(scroll);

        double[] x = poly.xVerts(scroll.x);
        double[] y = poly.yVerts(scroll.y);
        VFX.glow(x, y, glow, 1);
    }

    public void drawBuff(Vector scroll, Point p) {
        p.subtract(scroll);
        VFX.glow(p, buffRad, glow, 0.25);
    }

    // subclasses --------------------------------------------------------------

    public static class HomingPowerup extends Buff {

        // constructors --------------------------------------------------------

        public HomingPowerup(double x, double y, double size) {
            super(x, y, 4, size, Constants.HOMING_DROP_TAG, HOMING_RAD);
            glow = Constants.HOMING_DROP_GLOW;
            value = 4;
        }

        // other methods -------------------------------------------------------

        public void apply(Entity e) {
            if (e.homeChance < Constants.TOLERANCE) {
                e.homeChance = 0.1;
            }
            else {
                e.homeChance += (1 - e.homeChance) * 0.1;
            }
        }
    }

    public static class DamageUp extends Buff {

        // constructors --------------------------------------------------------

        public DamageUp(double x, double y, double size) {
            super(x, y, 7, size, Constants.DAMAGE_DROP_TAG, DAMAGE_RAD);
            glow = Constants.DAMAGE_DROP_GLOW;
            value = 3;
        }

        // other methods -------------------------------------------------------

        public void apply(Entity e) {
            e.dmg += e.dmg / INCR_SCALE;
        }
    }

    public static class HealthUp extends Buff {

        // constructors --------------------------------------------------------

        public HealthUp(double x, double y, double size) {
            super(x, y, 6, size, Constants.HEALTH_DROP_TAG, HEALTH_RAD);
            glow = Constants.HEALTH_DROP_GLOW;
        }

        // other methods -------------------------------------------------------

        public void apply(Entity e) {
            e.hp += (e.hpStat - e.hp) / 5;
            e.hpStat += INCR_SCALE / e.hpStat;
        }

    }

    public static class SpeedUp extends Buff {

        // constructors --------------------------------------------------------

        public SpeedUp(double x, double y, double size) {
            super(x, y, 5, size, Constants.SPEED_DROP_TAG, SPEED_RAD);
            glow = Constants.SPEED_DROP_GLOW;
        }

        // other methods -------------------------------------------------------

        public void apply(Entity e) {
            e.max_speed += 0.05;
        }

    }

    public static class ReloadBoost extends Buff {

        // constructors --------------------------------------------------------

        public ReloadBoost(double x, double y, double size) {
            super(x, y, 3, size, Constants.RELOAD_DROP_TAG, RELOAD_RAD);
            glow = Constants.RELOAD_DROP_GLOW;
        }

        // other methods -------------------------------------------------------

        public void apply(Entity e) {
            if (e.getTag() == Constants.ASTEROID_TAG) {
                return;
            }
            e.reloadTime *= 0.9;
            e.setTimerDuration(Constants.RELOAD_KEY, e.reloadTime);
        }
    }

    public static void main(String[] args) {

    }
}
