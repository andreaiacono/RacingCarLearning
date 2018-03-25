package me.andreaiacono.racinglearning.core;

import me.andreaiacono.racinglearning.gui.CircuitPanel;

public class Game {

    public final Car car;
    public final CircuitPanel circuit;

    public Game(Car car, CircuitPanel circuit) {
        this.car = car;
        this.circuit = circuit;
    }

    public void updateCarPosition() {
        car.setIsOnTrack(circuit.isCarOnTrack());
        car.updatePosition();
    }

    public byte[] getScreenFrame() {
        return circuit.getCurrentFrame();
    }

    public void reset() {
        car.reset();
        circuit.reset();
    }

    public boolean isOver() {
        return !circuit.isCarInsideScreen() || circuit.isLapCompleted();
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
