import java.awt.Color;

/*
Stores global game constants (so much easier than keeping track of tiny things)
stared at java color api for a while for no good reason
 */
public class Constants {

    public static final Color GAME_BG_COLOR = new Color(20, 20, 20);
    public static final Color PRIMARY_COLOR = new Color(150, 150, 150);
    public static final Color ACCENT_COLOR = new Color(50, 50, 50);

    public static final Vector ACCENT_OFFSET = new Vector(0.75, 0.75);

    public static final Color PLAYER_GLOW = new Color(0, 200, 255, 15);
    public static final Color ENEMY_GLOW = new Color(255, 0, 0, 15);

    public static final Color P_MID_HP = new Color(255, 223, 0, 10);
    public static final Color P_LOW_HP = new Color(255, 0, 0, 10);

    public static final Color HOMING_DROP_GLOW = new Color(240, 230, 120, 5);
    public static final Color DAMAGE_DROP_GLOW = new Color(255, 0, 255, 5);
    public static final Color HEALTH_DROP_GLOW = new Color(0, 255, 0, 5);
    public static final Color SPEED_DROP_GLOW = new Color(0, 0, 255, 5);
    public static final Color RELOAD_DROP_GLOW = new Color(255, 160, 0, 5);

    public static final Color LUCKY_GLOW = new Color(255, 215, 0, 10);

    public static final char HOMING_DROP_TAG = 'h';
    public static final char DAMAGE_DROP_TAG = 'D';
    public static final char HEALTH_DROP_TAG = 'H';
    public static final char SPEED_DROP_TAG = 'S';
    public static final char RELOAD_DROP_TAG = 'R';

    public static final double BASE_BUFF_PARTICLE_CHANCE = 0.1;

    public static final double SCALE = 100;

    // float tolerance
    public static final double TOLERANCE = 1.0e-5;

    public static final double PLAYER_SIZE = SCALE * 0.03;
    public static final double ENEMY_SIZE = SCALE * 0.03;
    public static final double DROP_SIZE = SCALE * 0.02;

    public static final char PLAYER_TAG = 'P';
    public static final char ENEMY_TAG = 'E';
    public static final char ASTEROID_TAG = 'A';

    public static final String RESPAWN_KEY = "respawn";
    public static final String INVULN_KEY = "invuln";
    public static final String SHOOT_KEY = "shoot";
    public static final String RELOAD_KEY = "reload";
    public static final String EXHAUST_KEY = "exhaust";

    public static final int PLAYER_EXPLOSTION_DMG = 100;

    public static void main(String[] args) {
    }
}
