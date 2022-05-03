import java.awt.Color;
import java.util.ArrayList;

public class Background {

    private class BackgroundObj extends BasePolygon {

        private double omega;
        private Color color;
        private Vector vel;
        private Rectangle aabb;

        public BackgroundObj(int sides, double size, Vector vel, double omega) {
            super(sides, size);
            this.vel = vel;
            this.omega = omega;
            aabb = new Rectangle(verts);
            int gray = (int) (StdRandom.uniform() * 4 + 18);
            color = new Color(gray, gray, gray);
        }

        public void update(double dt) {
            rotate(omega * dt);
            translate(Vector.scale(vel, dt));
            aabb.reencompass(verts);
        }
    }

    private ArrayList<BackgroundObj> objs = new ArrayList<>();

    public Background() {
        for (int i = 0; i < 5; i++) {
            int sides = StdRandom.uniform(4, 6);
            double scale = Constants.SCALE;
            double size = StdRandom.uniform(scale * 0.25, scale * 0.5);
            Vector vel = new Vector(StdRandom.uniform(-1.0, 1.0),
                                    StdRandom.uniform(-1.0, 1.0));
            double omega = StdRandom.uniform(-3.0, 3.0);
            BackgroundObj bgObg = new BackgroundObj(sides, size, vel, omega);
            bgObg.recenter(scale / 2, scale / 2);
            objs.add(bgObg);
        }
    }

    public void update(double dt) {
        for (BackgroundObj obj : objs) {
            obj.update(dt);
            if (obj.aabb.getLft() < 0) {
                obj.vel.x *= -1;
            }
            else if (obj.aabb.getRht() > Constants.SCALE) {
                obj.vel.x *= -1;
            }

            if (obj.aabb.getTop() > Constants.SCALE) {
                obj.vel.y *= -1;
            }
            else if (obj.aabb.getBot() < 0) {
                obj.vel.y *= -1;
            }
            obj.drawFill(new Vector(), obj.color);
        }
    }

    public static void main(String[] args) {

    }
}
