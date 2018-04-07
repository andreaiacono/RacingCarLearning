package me.andreaiacono.racinglearning.core;

import java.awt.*;

import static me.andreaiacono.racinglearning.gui.TrackPanel.CAR_STARTING_ANGLE;

public class Car {

    private static final double AUTO_SLOW_DOWN = 0.2;
    public static final int BIG_MAX_SPEED = 7;
    public static final int SMALL_MAX_SPEED = 4;
    private static final int MAX_STEERING_ANGLE = 45;
    private boolean isOnTrack;

    private static final double ADHERENCE = 0.2;

    // the velocity of the car is a 2D vector formed by the speed and the direction
    // the car is going (which differs from the steering angle)
    private Velocity velocity;

    // the steering angle
    private double steeringAngle = 0;
    private double adjustmentAngle = 0;

    // the position of the car (as of the middle point)
    private double x, y;
    private Point startingPosition;
    private int maxSpeed;

    public void accelerate(double qty) {
        velocity.speed = Math.min(velocity.speed + qty, maxSpeed);
    }

    public void brake(double qty) {
        velocity.speed = Math.max(velocity.speed - qty, 0);
    }

    public void steer(double angle) {

        double heading = Math.max(-MAX_STEERING_ANGLE, Math.min(angle, MAX_STEERING_ANGLE));
        velocity.direction = (velocity.direction + heading) % 360.0;

//        steeringAngle = Math.max(-MAX_STEERING_ANGLE, Math.min(angle, MAX_STEERING_ANGLE));
//        adjustmentAngle = steeringAngle;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public double getDirection() {
        return velocity.direction;
    }

    public void applyDirections(Command command) {

        if (command.getFrontalValue() == 1) {
            accelerate(0.4);
        }
        else if (command.getFrontalValue() == -1) {
            brake(0.8);
        }

        if (command.getLateralValue() != 0) {
            steer(8 * command.getLateralValue());
        }
    }

    public void updatePosition() {

        // updates the car direction according to its velocity and the steering angle
//        double difference = (steeringAngle - adjustmentAngle / velocity.speed);
//        if (Math.abs(difference) < 0.001) {
//            velocity.direction = velocity.direction + difference % 360.0; //  * ADHERENCE;
//        }


        // updates car coords according to its velocity
//        double directionInRadians = Math.toRadians(velocity.direction);
//        double steeringInRadians = Math.toRadians(steeringAngle);
//        x += Math.cos(directionInRadians) * velocity.speed;
//        y += Math.sin(directionInRadians) * velocity.speed;
//        double deltaTime = 1;
//
//        double forwardsSpeed = x * Math.cos(directionInRadians) + y * Math.sin(steeringInRadians);
//        double lateralSpeed = y * Math.sin(directionInRadians) - y * Math.cos(steeringInRadians);
//        if ( lateralSpeed > 0 )
//            lateralSpeed = Math.max( 0.0, lateralSpeed - 7*deltaTime );
//        else
//            lateralSpeed = Math.min( 0.0, lateralSpeed + 7*deltaTime );

// combine components back into velocity
//       x = forwardsSpeed*Math.cos(steeringAngle) + lateralSpeed*Math.sin(steeringAngle);
//        y = forwardsSpeed*Math.sin(steeringAngle) - lateralSpeed*Math.cos(steeringAngle);

//
//
//        Vector2D forwardVelocity = car.Forward * Vector2D.Dot(car.Velocity, car.Forward);
//        Vector2D rightVelocity = car.Right * Vector2D.Dot(car.Velocity, car.Right);
//        car.Velocity = forwardVelocity + rightVelocity * drift;

        x += Math.cos(Math.toRadians(velocity.direction)) * velocity.speed;
        y += Math.sin(Math.toRadians(velocity.direction)) * velocity.speed;

        // if not accelerating, the car slows down until stopped
        brake(AUTO_SLOW_DOWN);

        // if the car is outside the track, it slows down more
        if (!isOnTrack) {
            brake(getVelocity().speed/4);
        }

        adjustmentAngle -= adjustmentAngle / 10;
    }

    public void reset() {
        x = startingPosition.x;
        y = startingPosition.y;
        adjustmentAngle = 0;
        velocity = new Velocity(0, CAR_STARTING_ANGLE);
    }

    public Velocity getVelocity() {
        return velocity;
    }

    public void setIsOnTrack(boolean isOnTrack) {
        this.isOnTrack = isOnTrack;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Car{");
        sb.append(velocity);
        sb.append(", x=").append((int) x);
        sb.append(", y=").append((int) y);
        sb.append('}');
        return sb.toString();
    }

    public void setStartingPosition(Point startingPosition) {

        this.startingPosition = startingPosition;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public static class Velocity {

        // the direction in degrees [0,360)
        public double direction;

        // the speed [0, 8]
        public double speed;

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
            final StringBuilder sb = new StringBuilder("Vel{");
            sb.append("dir=").append((int) direction);
            sb.append(", speed=").append((int) speed);
            sb.append('}');
            return sb.toString();
        }

    }
}

