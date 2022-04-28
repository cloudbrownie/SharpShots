public class Clock {

    // store the when clock was started
    private double startTime;

    // store previous time recorded
    private double prevTime;

    // rate for frame rate independence
    private double fpsRate = 60;

    // default constructor
    public Clock() {
    }

    // log the current time
    public void start() {
        startTime = prevTime = System.currentTimeMillis();
    }

    // return elapsed time from last logged start time
    public double elapsed() {
        return System.currentTimeMillis() - startTime;
    }

    // return the elapsed since the last tick call as scalar of fpsRate
    public double tick() {
        double currTime = System.currentTimeMillis();
        double dt = currTime - prevTime;
        dt /= fpsRate;
        prevTime = currTime;

        return dt;
    }

    public static void main(String[] args) {
        // test if clock is good

        Clock clock = new Clock();

        clock.start();
        try {
            Thread.sleep(20);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        StdOut.println(clock.elapsed());

        clock.tick();
    }
}
