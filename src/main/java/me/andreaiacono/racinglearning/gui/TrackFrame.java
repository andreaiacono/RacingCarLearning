package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.core.Game;

import javax.swing.*;

public class TrackFrame extends JFrame {
    public TrackFrame(Game game, int size, float scale) {
        super("Racing Car");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(game.track);
        setSize((int) (size * scale), (int) (size * scale) + 37);
        setLocation(100, 50);
    }
}
