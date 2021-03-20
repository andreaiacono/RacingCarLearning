package me.andreaiacono.racinglearning.track;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Random;

public class RandomTrackDemo extends JFrame implements ChangeListener {


    private final RandomTrackPanel randomTrackPanel;
    private final JLabel statusLabel;
    private int SCALE = 1;
    private int GRID = 4;
    private int SIZE = 40;

    private final int SEED = 1;
    public RandomTrackDemo() {

        super("Random Track Demo");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


//        SpringLayout layout = new SpringLayout();
        randomTrackPanel = new RandomTrackPanel(SEED, SIZE, GRID, SCALE);

        JPanel panel = new JPanel();

        JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 4, 500, 40);
        sizeSlider.addChangeListener(this);
        sizeSlider.setName("size");
        panel.add(sizeSlider);

        JSlider gridSlider = new JSlider(JSlider.HORIZONTAL, 2, 20, 4);
        gridSlider.addChangeListener(this);
        gridSlider.setName("grid");
        panel.add(gridSlider);

        JSlider scaleSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        scaleSlider.addChangeListener(this);
        scaleSlider.setName("scale");
        panel.add(scaleSlider);

        JSplitPane divider = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, randomTrackPanel, panel);
        divider.setDividerLocation(350);

        JButton button = new JButton("New Track");
        button.addActionListener(event -> randomTrackPanel.newTrack());
        panel.add(button);

        add(divider);


        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(getWidth(), 16));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel = new JLabel("status");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);


        setSize(600, 250);
        setVisible(true);

    }

    public static void main(String[] args) {
        new RandomTrackDemo();
    }

    @Override
    public void stateChanged(ChangeEvent e) {

        if (((JSlider) e.getSource()).getName().equals("size")) {
            SIZE = ((JSlider) e.getSource()).getValue();
        } else if (((JSlider) e.getSource()).getName().equals("grid")) {
            GRID = ((JSlider) e.getSource()).getValue();
        } else if (((JSlider) e.getSource()).getName().equals("scale")) {
            SCALE = ((JSlider) e.getSource()).getValue();
        }
        randomTrackPanel.update(SIZE, GRID, SCALE);
        statusLabel.setText(String.format("Size: %d, Grid: %d, Scale: %d", SIZE, GRID, SCALE));
        randomTrackPanel.repaintTrack();
        repaint();
    }
}

class RandomTrackPanel extends JPanel {

    private Image randomTrack;
    private int size;
    private int grid;
    private int scale;
    private int seed;

    public RandomTrackPanel(int seed, int size, int grid, int scale) {
        super();
        this.seed = seed;
        this.size = size;
        this.grid = grid;
        this.scale = scale;
        repaintTrack();
    }

    void update(int size, int grid, int scale) {
        this.size = size;
        this.grid = grid;
        this.scale = scale;
    }

    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.drawImage(randomTrack.getScaledInstance((int) (size * scale), (int) (size * scale), Image.SCALE_FAST), 0, 0, (int) (size * scale), (int) (size * scale), null);
    }

    public void newTrack() {
        seed = new Random().nextInt();
        randomTrack = new RandomRaceTrack().getRandomTrack(size, grid, seed);
        repaint();
    }

    public void repaintTrack() {
        randomTrack = new RandomRaceTrack().getRandomTrack(size, grid, seed);
        repaint();
    }
}