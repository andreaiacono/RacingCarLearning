package me.andreaiacono.racinglearning.core;

public class Car {

    private static final int MAX_SPEED = 10;
    private static final int MAX_STEERING_ANGLE = 35;


    private double heading;
    private double speed;

    private int x, y;

    public Car(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void accelerate(double qty) {
        speed = Math.min(speed + qty, MAX_SPEED);
    }

    public void brake(double qty) {
        speed = Math.max(speed - qty, 0);
    }

    public void steer(double angle) {
        heading = Math.max(-MAX_STEERING_ANGLE, Math.min(heading+angle, MAX_STEERING_ANGLE));
    }

    public double getHeading() {
        return heading;
    }

    public double getSpeed() {
        return speed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void updatePosition() {
        x+=speed;
        brake(0.2);
    }

    @Override
    public String toString() {
        return "{\"Car\":{"
                + "                        \"heading\":\"" + heading + "\""
                + ",                         \"speed\":\"" + speed + "\""
                + ",                         \"x\":\"" + x + "\""
                + ",                         \"y\":\"" + y + "\""
                + "}}";
    }
}

