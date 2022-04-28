import java.awt.Color;

public class SharpShots {

    public static void main(String[] args) {

        // initialize stddraw stuff
        double scale = 100;
        StdDraw.setScale(0, scale);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenRadius(0.005);

        // create the player, also use this reference to refer the player
        Player p = new Player(scale / 2, scale / 2, scale * 0.02);

        // init target of all enemies
        Enemy.setTarget(p);

        // entity handler
        EntityHandler bartholemew = new EntityHandler(p);

        // create object to handle vfx since their logic is pretty separate
        VFX vfx = new VFX();

        // clock obj for delta time
        Clock clock = new Clock();

        // background color
        Color bgColor = new Color(50, 50, 50);

        // scrolling values to follow the player around
        Vector scroll = new Vector();

        // store the clock and game loop
        clock.start();
        while (true) {

            // find delta time between frames
            double dt = clock.tick();

            // clear the window
            StdDraw.clear(bgColor);

            // randomly generate asteroids
            if (StdRandom.uniform(1000) < 50) {
                bartholemew.addAsteroid(Asteroid.genRandomAsteroid(scale));
            }

            // handle player inputs
            p.handleInputs();

            // shoot a bullet if player shot
            if (p.hasShot()) {
                Bullet b = p.shoot();
                bartholemew.addProjectile(b);
            }

            // update all entity stuff
            bartholemew.update(dt, scroll);

            // draw the effects
            vfx.update(scroll, dt);


            StdDraw.show();
            StdDraw.pause(20);
        }
    }
}
