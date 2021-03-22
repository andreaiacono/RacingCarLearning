package me.andreaiacono.racinglearning.track;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Track {
    private final BufferedImage image;
    private final List<Polygon> checkpoints;

    public Track(BufferedImage image, List<Polygon> checkpoints) {
        this.image = image;
        this.checkpoints = checkpoints;
    }

    public BufferedImage getImage() {
        return image;
    }

    public List<Polygon> getCheckpoints() {
        return checkpoints;
    }
}
