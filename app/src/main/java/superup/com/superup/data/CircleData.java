package superup.com.superup.data;

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

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    public int getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(int sweepAngle) {
        this.sweepAngle = sweepAngle;
    }
}
