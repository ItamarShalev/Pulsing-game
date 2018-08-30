package superup.com.superup.data;

/**
 * This class data contains the startAngle and sweepAngle for drawing circle
 */
public class CircleData {

    private int startAngle;
    private int sweepAngle;

    public CircleData(int startAngle, int sweepAngle) {
        this.startAngle = startAngle;
        this.sweepAngle = sweepAngle;
    }

    public int getStartAngle() {
        return startAngle;
    }

    public int getSweepAngle() {
        return sweepAngle;
    }

}
