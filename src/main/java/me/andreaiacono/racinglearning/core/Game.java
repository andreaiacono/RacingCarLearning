package me.andreaiacono.racinglearning.core;

import me.andreaiacono.racinglearning.gui.CircuitPanel;

public class Game {

    private int epoch;
    public final Car car;
    public final CircuitPanel circuit;

    public Game(Car car, CircuitPanel circuit) {
        this.car = car;
        this.circuit = circuit;
    }

    public byte[] getScreenFrame() {
        return circuit.getCurrentFrame();
    }

    public void reset() {
        System.out.println("Epoch #" + epoch++);
        car.reset();
        circuit.reset();
    }

    public boolean isOver() {
        return !circuit.isCarInsideScreen() || circuit.isLapCompleted() || circuit.isTimeOver();
    }

    public int move(Command command) {

        car.applyDirections(command);

        // updates the position of the car
        car.setIsOnTrack(circuit.isCarOnTrack());
        car.updatePosition();

        // refreshes the screen with the new position
        circuit.updateCircuit();

        return circuit.getReward();
    }
}
