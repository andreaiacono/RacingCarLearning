package me.andreaiacono.racinglearning.misc;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DrivingKeyListener implements KeyListener {

    private final Directions directions = new Directions();

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
                directions.upPressed = isPressed;
                break;
            case KeyEvent.VK_DOWN:
                directions.downPressed = isPressed;
                break;
            case KeyEvent.VK_LEFT:
                directions.leftPressed = isPressed;
                break;
            case KeyEvent.VK_RIGHT:
                directions.rightPressed = isPressed;
                break;
        }
//        System.out.print("\r  up pressed: " + directions.upPressed + ", down pressed: " + directions.downPressed +", leftPressed: " + directions.leftPressed + " rightPressde: " + directions.rightPressed);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println("key typed: " + e);
    }

    public Directions getDirections() {
        return directions;
    }
}
