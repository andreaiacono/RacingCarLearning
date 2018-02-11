package me.andreaiacono.racinglearning.core;


public class Car {

    private static final double AUTO_SLOW_DOWN = 0.2;
    private static final int MAX_SPEED = 10;
    private static final int MAX_STEERING_ANGLE = 45;

    // the velocity of the car is a 2D vector formed by the speed and the direction
    private Velocity velocity = new Velocity(0, 0);

    private double x, y;

    public Car(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void accelerate(double qty) {
        velocity.speed = Math.min(velocity.speed + qty, MAX_SPEED);
    }

    public void brake(double qty) {
        velocity.speed = Math.max(velocity.speed - qty, 0);;
    }

    public void steer(double angle) {
        double heading = Math.max(-MAX_STEERING_ANGLE, Math.min(angle, MAX_STEERING_ANGLE));
        velocity.direction = (velocity.direction + heading) % 360.0;
   }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public void updatePosition() {
        x += Math.cos(Math.toRadians(velocity.direction)) * velocity.speed;
        y += Math.sin(Math.toRadians(velocity.direction)) * velocity.speed;

        brake(AUTO_SLOW_DOWN);
    }

    public Velocity getVelocity() {
        return velocity;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Car{");
        sb.append(velocity);
        sb.append(", x=").append(x);
        sb.append(", y=").append(y);
        sb.append('}');
        return sb.toString();
    }

    static class Velocity {

        // the direction in degrees
        double direction;
        double speed;

        public Velocity(double speed, double direction) {
            this.speed = speed;
            this.direction = direction;
        }

        public Velocity normalize() {
            double size = Math.sqrt(speed * speed + direction * direction);
            return new Velocity(speed / size, direction / size);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Velocity{");
            sb.append("direction=").append(direction);
            sb.append(", speed=").append(speed);
            sb.append('}');
            return sb.toString();
        }
    }
}

