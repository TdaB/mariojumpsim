package mariojumpsim;

public class Physics {
    private Physics() {}

    public static double updateVelocity(double initialV, double accel, double t, double maxV) {
        double finalV = initialV + (accel * t);
        if ((initialV < 0 && finalV > 0) || (initialV > 0 && finalV < 0)) {
            return 0.0;
        } else if (finalV < -1.0 * maxV) {
            return -1.0 * maxV;
        } else if (finalV > maxV) {
            return maxV;
        } else {
            return finalV;
        }
    }

    public static double distance(double initialV, double finalV, double t) {
        return (initialV + finalV) * .5 * t;
    }
}
