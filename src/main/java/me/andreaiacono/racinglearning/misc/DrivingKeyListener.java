package me.andreaiacono.racinglearning.misc;

import me.andreaiacono.racinglearning.core.Car;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DrivingKeyListener implements KeyListener {

    private final Car car;
    boolean upPressed, downPressed, leftPressed, rightPressed;

    public DrivingKeyListener(Car car) {
        this.car = car;
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        switchKeys(ke, true);
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        switchKeys(ke, false);
    }

    void switchKeys(KeyEvent keyEvent, boolean isPressed) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_UP:
                upPressed = isPressed;
                break;
            case KeyEvent.VK_DOWN:
                downPressed = isPressed;
                break;
            case KeyEvent.VK_LEFT:
                leftPressed = isPressed;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = isPressed;
                break;
        }
        System.out.print("\r" + car + "  up pressed: " + upPressed + ", down pressed: " + downPressed +", leftPressed: " + leftPressed + " rightPressde: " + rightPressed);
        if (upPressed) {
            car.accelerate(0.4);
        }
        if (downPressed) {
            car.brake(0.8);
        }
        if (leftPressed) {
            car.steer(-8);
        }
        if (rightPressed) {
            car.steer(8);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
