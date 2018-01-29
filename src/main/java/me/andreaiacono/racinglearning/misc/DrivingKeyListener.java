package me.andreaiacono.racinglearning.misc;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DrivingKeyListener implements KeyListener {

    boolean upPressed, downPressed, leftPressed, rightPressed;

    @Override
    public void keyTyped(KeyEvent e) {

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
        System.out.print("\rup pressed: " + upPressed + " , down pressed: " + downPressed +" ,  leftPressed: " + leftPressed + " rightPressde: " + rightPressed);
    }
}
