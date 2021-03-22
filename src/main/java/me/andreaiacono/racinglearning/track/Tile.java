package me.andreaiacono.racinglearning.track;

public enum Tile {

    LEFT_TO_DOWN(Side.LEFT, Side.DOWN, '┐'),
    LEFT_TO_UP(Side.LEFT, Side.UP, '┘'),
    RIGHT_TO_DOWN(Side.RIGHT, Side.DOWN, '┌'),
    RIGHT_TO_UP(Side.RIGHT, Side.UP, '└'),
    UP_TO_RIGHT(Side.UP, Side.RIGHT, '└'),
    UP_TO_LEFT(Side.UP, Side.LEFT, '┘'),
    DOWN_TO_LEFT(Side.DOWN, Side.LEFT, '┐'),
    DOWN_TO_RIGHT(Side.DOWN, Side.RIGHT, '┌'),
    LEFT_TO_RIGHT(Side.LEFT, Side.RIGHT, '─'),
    RIGHT_TO_LEFT(Side.RIGHT, Side.LEFT, '─'),
    UP_TO_DOWN(Side.UP, Side.DOWN, '│'),
    DOWN_TO_UP(Side.DOWN, Side.UP, '│');

    private Side in;
    private Side out;
    private char c;

    Tile(Side in, Side out, char c) {
        this.in = in;
        this.out = out;
        this.c = c;
    }

    public Side getInput() {
        return in;
    }

    public Side getOutput() {
        return out;
    }

    public char getChar() {
        return c;
    }

}




