import java.awt.Font;
import java.awt.event.KeyEvent;

public class SharpShots {

    private static Point genPositionOffscreen(Vector scroll) {
        double x, y;
        double scale = Constants.SCALE;

        if (StdRandom.uniform(-1.0, 1.0) > 0) {
            x = scale * 1.05 + StdRandom.uniform() * scale * 0.2;
        }
        else {
            x = -scale * 0.05 - StdRandom.uniform() * scale * 0.2;
        }

        if (StdRandom.uniform(-1.0, 1.0) > 0) {
            y = scale * 1.05 + StdRandom.uniform() * scale * 0.2;
        }
        else {
            y = -scale * 0.05 - StdRandom.uniform() * scale * 0.2;
        }

        return new Point(x + scroll.x, y + scroll.y);
    }

    private static Enemy genEnemy(Vector scroll) {
        Point pos = genPositionOffscreen(scroll);

        return new Enemy(pos.x, pos.y, Constants.ENEMY_SIZE);
    }

    private static Asteroid genAsteroid(Vector scroll) {
        Point pos = genPositionOffscreen(scroll);

        return Asteroid.genRandomAsteroid(pos, Constants.SCALE);
    }

    private static void displayStartScreen() {
        Font bigFont = new Font("Courier New", Font.PLAIN, 30);
        Font smallFont = new Font("Courier New", Font.PLAIN, 20);

        double scale = Constants.SCALE;

        Asteroid asteroid = Asteroid.genAsteroid(scale * 0.33,
                                                 scale * 0.52,
                                                 new Vector(), scale * 0.1);
        Enemy enemy = new Enemy(scale * 0.66, scale * 0.52, scale * 0.05);

        double buffSize = scale * 0.05;
        double buffX = scale * 0.15;
        double buffXShift = scale * 0.18;
        double buffY = scale * 0.25;
        Buff.HealthUp hp = new Buff.HealthUp(buffX, buffY, buffSize);
        buffX += buffXShift;
        Buff.DamageUp dmg = new Buff.DamageUp(buffX, buffY, buffSize);
        buffX += buffXShift;
        Buff.SpeedUp spd = new Buff.SpeedUp(buffX, buffY, buffSize);
        buffX += buffXShift;
        Buff.ReloadBoost rld = new Buff.ReloadBoost(buffX, buffY, buffSize);
        buffX += buffXShift;
        Buff.HomingPowerup hom = new Buff.HomingPowerup(buffX, buffY, buffSize);

        double buffTextY = buffY + scale * 0.1;

        Clock clock = new Clock();

        Vector scroll = new Vector();

        clock.start();
        while (true) {

            double dt = clock.tick();

            // display instructions
            StdDraw.clear(Constants.GAME_BG_COLOR);

            StdDraw.setPenColor(Constants.PRIMARY_COLOR);

            StdDraw.setFont(bigFont);

            Point disp = new Point(scale * 0.05, scale * 0.95);
            double shiftDown = scale * 0.05;
            StdDraw.text(scale * 0.5, disp.y, "Sharp Shots");

            StdDraw.setFont(smallFont);

            disp.y -= shiftDown;
            StdDraw.textLeft(disp.x, disp.y, "To Play: ");
            disp.y -= shiftDown;
            StdDraw.textLeft(disp.x, disp.y, "- Thrust: W or UP arrow");
            disp.y -= shiftDown;
            StdDraw.textLeft(disp.x, disp.y, "- Turn:   A/D or LEFT/RIGHT arrow");
            disp.y -= shiftDown;
            StdDraw.textLeft(disp.x, disp.y, "- Reload: S or DOWN arrow");
            disp.y -= shiftDown;
            StdDraw.textLeft(disp.x, disp.y, "- Shoot:  SPACE");

            StdDraw.text(scale * 0.5, scale * 0.1, "Press Enter to Play");

            StdDraw.text(scale * 0.33, scale * 0.65, "Asteroid:");

            asteroid.draw(scroll);
            asteroid.rotate(5 * dt);

            StdDraw.text(scale * 0.66, scale * 0.65, "Enemy:");

            enemy.draw(scroll);
            enemy.rotate(5 * dt);


            StdDraw.text(scale * 0.5, scale * 0.4, "Buffs:");

            double dispX = scale * 0.15;
            StdDraw.text(dispX, buffTextY, "HP:");
            dispX += buffXShift;
            StdDraw.text(dispX, buffTextY, "DMG:");
            dispX += buffXShift;
            StdDraw.text(dispX, buffTextY, "SPD:");
            dispX += buffXShift;
            StdDraw.text(dispX, buffTextY, "RELOAD:");
            dispX += buffXShift;
            StdDraw.text(dispX, buffTextY, "HOMING:");
            hp.draw(scroll);
            dmg.draw(scroll);
            spd.draw(scroll);
            hom.draw(scroll);
            rld.draw(scroll);


            StdDraw.show();

            if (StdDraw.isKeyPressed(KeyEvent.VK_ENTER)) {
                return;
            }
        }
    }

    public static void main(String[] args) {

        // initialize stddraw stuff
        double scale = Constants.SCALE;
        StdDraw.setScale(0, scale);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenRadius(0.002);

        // display beginning menu first
        displayStartScreen();

        // try to use cool font
        Font font = new Font("Courier New", Font.PLAIN, 30);
        StdDraw.setFont(font);

        // clock obj for delta time
        Clock clock = new Clock();
        Clock.Timer TIMER = new Clock.Timer();

        // background stuff
        Background bg = new Background();

        // generator times for asteroids and enemies
        String asteroidKey = "asteroids";
        String enemyKey = "enemies";

        TIMER.addTimer(asteroidKey);
        TIMER.addTimer(enemyKey);
        TIMER.randomizeDuration(asteroidKey, 2000, 200);
        TIMER.randomizeDuration(enemyKey, 5000, 200);

        // create the player, also use this reference to refer the player
        Player p = new Player();

        // init target of all enemies
        Enemy.setTarget(p);
        Enemy.startClock();

        // entity handler
        EntityHandler eHandler = new EntityHandler(p);

        // scrolling values to follow the player around
        Vector scroll = new Vector();
        double scrollSpeedFactor = 3;

        // store the clock and game loop
        clock.start();
        while (true) {

            // find delta time between frames
            double dt = clock.tick();
            double mins = clock.elapsedMins();

            // clear the window
            StdDraw.clear(Constants.GAME_BG_COLOR);

            // draw some cool stuff in the background so it isn't static and sad
            bg.update(dt);

            // randomly generate asteroids
            if (TIMER.checkTimer(asteroidKey)) {
                eHandler.addAsteroid(genAsteroid(scroll));
                TIMER.randomizeDuration(asteroidKey, 800, 200);
            }

            // randomly generate enemies
            if (TIMER.checkTimer(enemyKey) && p.getLives() > 0
                    && eHandler.getNumberOfEnemies() < 5) {
                eHandler.addEnemy(genEnemy(scroll));
                double lowerTime = Math.min(10000 / mins, 5000);
                double upperTime = Math.min(16000 / mins, 8000);
                TIMER.randomizeDuration(enemyKey, upperTime, lowerTime);
            }

            // update all entity stuff
            eHandler.update(dt, scale, scroll);

            // update the scroll value
            Point pc = p.center();
            scroll.x += (pc.x - scroll.x - scale / 2) / scrollSpeedFactor * dt;
            scroll.y += (pc.y - scroll.y - scale / 2) / scrollSpeedFactor * dt;

            // write the player's score to the screen
            Point tp = new Point(scale * 0.01, scale * 0.95);
            String score = String.format("SCORE : %.2f", p.getScore());

            if (p.getLives() > 0) {
                StdDraw.setPenColor(Constants.ACCENT_COLOR);
                StdDraw.textLeft(tp.x - Constants.ACCENT_OFFSET.x,
                                 tp.y - Constants.ACCENT_OFFSET.y, score);
                StdDraw.setPenColor(Constants.PRIMARY_COLOR);
                StdDraw.textLeft(tp.x, tp.y, score);
            }
            else {
                StdDraw.setPenColor(Constants.ACCENT_COLOR);
                tp.x = scale * 0.5;
                tp.y = scale * 0.6;
                StdDraw.text(tp.x - Constants.ACCENT_OFFSET.x,
                             tp.y - Constants.ACCENT_OFFSET.y, score);
                tp.y = scale * 0.5;
                StdDraw.text(tp.x - Constants.ACCENT_OFFSET.x,
                             tp.y - Constants.ACCENT_OFFSET.y, "Play Again?");
                tp.y = scale * 0.4;
                StdDraw.text(tp.x - Constants.ACCENT_OFFSET.x,
                             tp.y - Constants.ACCENT_OFFSET.y,
                             "(Press Enter)");

                StdDraw.setPenColor(Constants.PRIMARY_COLOR);
                tp.y = scale * 0.6;
                StdDraw.text(tp.x, tp.y, score);
                tp.y = scale * 0.5;
                StdDraw.text(tp.x, tp.y, "Play Again?");
                tp.y = scale * 0.4;
                StdDraw.text(tp.x, tp.y, "(Press Enter)");
                if (StdDraw.isKeyPressed(KeyEvent.VK_ENTER)) {
                    p = new Player();
                    Enemy.setTarget(p);
                    Enemy.startClock();
                    eHandler.restart(p);
                }
            }

            p.drawLives(scale * 0.95, scale * 0.95);

            p.drawHealth(scale * 0.98, scale * 0.9, scale);

            p.drawAmmo(scale * 0.98, scale * 0.85, scale);

            StdDraw.show();
            StdDraw.pause(20);
        }
    }
}
