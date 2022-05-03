import java.awt.Color;
import java.util.ArrayList;

public class Homing extends Bullet {

    // instance vars -----------------------------------------------------------

    // homing bullet attrs
    private Entity target;
    private char targetTag;
    private double speed;
    private double angleRange;
    private double range = 30;
    private double homeWeight;

    // constructors ------------------------------------------------------------

    // basic constructor
    public Homing(Point pos, double degrees, double speed, double dmg,
                  char ownerTag, Color glow, double angleRange) {

        super(pos, degrees, speed, dmg, ownerTag, glow);

        this.speed = speed;
        this.angleRange = angleRange;

        sides = 3;
        poly = new CollidablePolygon(BasePolygon.genShape(sides, size));
        poly.recenter(pos);
        homeWeight = 0.1;
        rotation = vel.rotAngleDeg();
    }

    // setters -----------------------------------------------------------------

    // set homing weight
    public void setHomeWeight(double weight) {
        if (Math.abs(homeWeight - 1) < 1.0e-7) {
            throw new IllegalArgumentException("Weight must be > 1.");
        }
        this.homeWeight = weight;
    }

    // set a target to an enemy
    public void setTarget(Entity e) {
        target = e;
    }

    // set a target to a type of entity
    public void setTarget(char e) {
        targetTag = e;
    }

    // other methods -----------------------------------------------------------

    // home in method
    public void homeOnTarget(ArrayList<Entity> entities, double dt) {
        // error if there are no targets
        if (target == null && targetTag == 0) {
            throw new RuntimeException("Homing bullet has no target.");
        }

        // copy the entities list
        ArrayList<Entity> targets = new ArrayList<>();
        for (Entity e : entities) {
            targets.add(e);
        }

        // target on target entity
        if (target != null) {
            Vector dist = new Vector(center(), target.center());
            if (dist.norm() <= angleRange) {
                double angle = dist.rotAngleDeg();

                if (Math.abs(angle - rotation) <= angleRange) {
                    double rotateAngle = (angle - rotation) / homeWeight * dt;
                    rotate(rotateAngle);
                    vel.add(Vector.scale(dist, dt / homeWeight));
                    vel.clamp(speed);
                }
            }
        }

        // target nearby entities with this tag
        else if (targetTag != 0) {
            Point c = center();

            // remove non-targetable entities and entities too far away
            targets.removeIf(e -> e.getTag() != targetTag ||
                    c.distanceTo(e.center()) > range);

            // skip if there are no entities
            if (targets.size() < 1) {
                return;
            }

            // find closest entity
            Entity closest = targets.get(0);
            double closestDist = c.distanceTo(closest.center());
            for (int i = 1; i < targets.size(); i++) {
                Entity e = targets.get(i);

                double d = c.distanceTo(e.center());
                if (d < closestDist) {
                    closest = e;
                    closestDist = d;
                }
            }

            // home in on closest
            Vector dist = new Vector(c, closest.center());
            if (dist.norm() <= angleRange) {
                double angle = dist.rotAngleDeg();

                if (Math.abs(angle - rotation) <= angleRange) {
                    double rotateAngle = (angle - rotation) / homeWeight * dt;
                    rotate(rotateAngle);
                    vel.add(Vector.scale(dist, dt / homeWeight));
                    vel.clamp(speed);
                }
            }
        }
    }

    // draw method
    public void draw(Vector scroll) {
        poly.drawOutline(scroll, Constants.PRIMARY_COLOR);

        Point c = center();
        c.subtract(scroll);
        VFX.glow(c, getSize() * 1.1, getGlow(), 1);
    }

    // draw underline method
    public void drawUnderline(Vector scroll) {
        poly.drawOutline(Vector.add(scroll, Constants.ACCENT_OFFSET),
                         Constants.ACCENT_COLOR);
    }

    public static void main(String[] args) {

    }
}
