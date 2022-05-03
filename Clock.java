// everything is in milliseconds
public class Clock {

    // static methods ----------------------------------------------------------

    public static double time() {
        return System.currentTimeMillis();
    }

    // instance vars -----------------------------------------------------------

    // store the when clock was started
    private double startTime;

    // store previous time recorded
    private double prevTime;

    // rate for frame rate independence
    private final double FPS_RATE = 60;

    // store the actual fps
    private double fps;

    // default constructor
    public Clock() {
        startTime = 0;
        prevTime = 0;
        fps = 0;
    }

    // return the fps
    public double getFps() {
        return fps;
    }

    // log the current time
    public void start() {
        startTime = prevTime = time();
    }

    // return elapsed time from last logged start time
    public double elapsed() {
        return time() - startTime;
    }

    // return elapsed in seconds
    public double elapsedSecs() {
        return elapsed() / 1000;
    }

    // return elapsed in minutes
    public double elapsedMins() {
        return elapsedSecs() / 60;
    }

    // return the elapsed since the last tick call as scalar of fpsRate
    public double tick() {
        double currTime = time();
        double dt = currTime - prevTime;
        fps = 1000 / dt;
        dt /= FPS_RATE;
        prevTime = currTime;

        return dt;
    }

    // subclass ----------------------------------------------------------------

    public static class Timer {

        // instance vars -------------------------------------------------------

        // symbol timer that can hold timers for other objects
        private ST<String, Double> timerDurations = new ST<>();
        private ST<String, Double> timerPreviouses = new ST<>();

        // other methods -------------------------------------------------------

        // check if a timer is done, if so, reset timer to now
        public boolean checkTimer(String key) {
            throwDNEError(key);

            double time = time();
            if (time - timerPreviouses.get(key) >= timerDurations.get(key)) {
                timerPreviouses.put(key, time);
                return true;
            }
            return false;
        }

        // silently check if a timer is done, don't reset the timer
        public boolean silentCheckTimer(String key) {
            throwDNEError(key);

            return time() - timerPreviouses.get(key) >= timerDurations.get(key);
        }

        // peek at a timer for the duration left
        public double peekTimeLeft(String key) {
            throwDNEError(key);

            double timeLeft = time() - timerPreviouses.get(key);
            return Math.min(timeLeft, timerDurations.get(key));
        }

        // add a timer
        public void addTimer(String key) {
            if (!timerDurations.contains(key)) {
                timerDurations.put(key, 0.0);
                timerPreviouses.put(key, time());
            }
        }

        // add a timer with a duration
        public void addTimer(String key, double duration) {
            if (!timerDurations.contains(key)) {
                timerDurations.put(key, duration);
                timerPreviouses.put(key, time());
            }
        }

        // remove a timer
        public void removeTimer(String key) {
            throwDNEError(key);

            timerDurations.remove(key);
            timerPreviouses.remove(key);
        }

        // set a last call time for a timer
        public void setCheck(String key) {
            throwDNEError(key);

            timerPreviouses.put(key, time());
        }

        // change a duration for a timer
        public void changeDuration(String key, double duration) {
            throwDNEError(key);


            timerDurations.put(key, duration);
        }

        // randomize a duration
        public void randomizeDuration(String key, double scale, double base) {
            throwDNEError(key);

            timerDurations.put(key, StdRandom.uniform() * (scale - base) + base);
        }

        // called in most methods to throw appropriate errors
        private void throwDNEError(String key) {
            if (!timerDurations.contains(key)) {
                String TIMER_DNE_ERROR = "Timer does not exist.";
                throw new IllegalArgumentException(TIMER_DNE_ERROR);
            }
        }
    }


    public static void main(String[] args) {
    }
}
