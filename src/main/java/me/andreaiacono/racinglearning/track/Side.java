package me.andreaiacono.racinglearning.track;

import java.util.Arrays;

public enum Side {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    private int x;
    private int y;

    Side(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Side getOpposite() {
        return Arrays.stream(Side.values()).filter(side -> this.x == side.x * -1 && this.y == side.y * -1).findFirst().get();
    }


}
