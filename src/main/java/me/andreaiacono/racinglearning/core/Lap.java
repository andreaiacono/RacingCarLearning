package me.andreaiacono.racinglearning.core;

import me.andreaiacono.racinglearning.gui.CircuitPanel;

public class Lap {

    public final Car car;
    public final CircuitPanel circuit;

    public Lap(Car car, CircuitPanel circuit) {
        this.car = car;
        this.circuit = circuit;
    }

    public void updateCarPosition() {
        car.setIsOnTrack(circuit.isCarOnTrack());
        car.updatePosition();
    }
}
