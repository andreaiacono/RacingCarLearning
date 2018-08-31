package me.andreaiacono.racinglearning.track;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class RandomTrackDemo extends JFrame {

    public RandomTrackDemo() {

        super("Random Track Demo");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        RandomTrackPanel panel = new RandomTrackPanel(8, 200, 5);
        add(panel, BorderLayout.CENTER);
        JButton button = new JButton("New Track");
        button.addActionListener(event -> panel.newTrack(new Random().nextInt()));
        add(button, BorderLayout.PAGE_END);
        setSize(100, 150);
        setVisible(true);
    }

    public static void main(String[] args) {
        new RandomTrackDemo();
    }
}

class RandomTrackPanel extends JPanel {

    private Image randomTrack;
    private int size;
    private int tileSideNumber;

    public RandomTrackPanel(int seed, int size, int tileSideNumber) {
        super();
        this.size = size;
        this.tileSideNumber = tileSideNumber;
        randomTrack = new RandomRaceTrack().getRandomTrack(size, tileSideNumber, seed);
    }

    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.drawImage(randomTrack, 0, 0, size, size, null);
    }

    public void newTrack(int seed) {
        randomTrack = new RandomRaceTrack().getRandomTrack(size, tileSideNumber, seed);
        repaint();
    }
}