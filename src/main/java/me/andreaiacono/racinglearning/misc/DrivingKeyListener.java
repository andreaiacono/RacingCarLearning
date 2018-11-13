package me.andreaiacono.racinglearning.misc;

import me.andreaiacono.racinglearning.core.Command;

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

    public Command getCommand() {
        int frontal = directions.upPressed ? 1 : directions.downPressed ? -1 : 0;
        int lateral = directions.rightPressed ? 1 : directions.leftPressed ? -1 : 0;
//        if (frontal == 0 && lateral == 0) {
//            return Command.ACCELERATE;
//        }
//        if (lateral != 0) {
//            frontal = 0;
//        }
        return Command.getCommand(frontal, lateral);
    }

    public class Directions {
        public boolean upPressed, downPressed, leftPressed, rightPressed;
    }

}
