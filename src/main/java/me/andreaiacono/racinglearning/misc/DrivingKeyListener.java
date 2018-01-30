package me.andreaiacono.racinglearning.misc;

import me.andreaiacono.racinglearning.core.Car;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DrivingKeyListener implements KeyListener {

    private final Car car;

    public DrivingKeyListener(Car car) {
        this.car = car;
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        switchKeys(ke);
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        switchKeys(ke);
    }

    void switchKeys(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_UP:
                car.accelerate(1);
                break;
            case KeyEvent.VK_DOWN:
                car.brake(1);
                break;
            case KeyEvent.VK_LEFT:
                car.steer(-5);
                break;
            case KeyEvent.VK_RIGHT:
                car.steer(5);
                break;
        }
        System.out.print("\r" + car);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
