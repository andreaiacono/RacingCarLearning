package me.andreaiacono.racinglearning.core;

public class Car {

    private double heading;
    private double speed;

    private int x, y;

    public Car(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void accelerate(double qty) {
        speed += qty;
    }

    public void brake(double qty) {
        speed -= qty;
        if (speed < 0) speed = 0;
    }

    public void steer(double angle) {
        heading = (heading + angle) % 360;
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
}

