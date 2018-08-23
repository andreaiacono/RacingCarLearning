package me.andreaiacono.racinglearning.track;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.awt.image.BufferedImage.TYPE_3BYTE_BGR;

public class RandomRaceTrack {

    public BufferedImage getRandomTrack(int size, int tilesSideNumber, int seed) {

        Tile[][] tiles = createNewTrack(tilesSideNumber, seed);
        return createImageFromTiles(size, tilesSideNumber, tiles);
    }

    private BufferedImage createImageFromTiles(int panelSize, int tilesSideNumber, Tile[][] tiles) {

        BufferedImage bufferedImage = new BufferedImage(panelSize, panelSize, TYPE_3BYTE_BGR);
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, panelSize, panelSize);

        int cellSize = panelSize / tilesSideNumber;
        int cellMid = cellSize / 2;

        int strokeSize = (int) (cellSize / 1.3);
        g.setStroke(new BasicStroke(strokeSize));
        g.setColor(Color.BLACK);
        int arcSize = cellSize;
        Image upToLeftArc = getUpToLeftAngle(arcSize, cellSize);
        Image upToRightArc = getUpToRightAngle(arcSize, cellSize);
        Image downToLeftArc = getDownToLeftAngle(arcSize, cellSize);
        Image downToRightArc = getDownToRightAngle(arcSize, cellSize);

        for (int i = 0; i < tilesSideNumber; i++) {
            for (int j = 0; j < tilesSideNumber; j++) {
                Tile tile = tiles[i][j];
                if (tile != null) {
                    switch (tile) {
                        case LEFT_TO_RIGHT:
                        case RIGHT_TO_LEFT:
                            g.drawLine(i * cellSize + strokeSize / 2, j * cellSize + cellMid, (i + 1) * cellSize - strokeSize / 2, j * cellSize + cellMid);
                            break;
                        case UP_TO_DOWN:
                        case DOWN_TO_UP:
                            g.drawLine(i * cellSize + cellMid, j * cellSize + strokeSize / 2, i * cellSize + cellMid, (j + 1) * cellSize - strokeSize / 2);
                            break;
                        case UP_TO_LEFT:
                        case LEFT_TO_UP:
                            g.drawImage(upToLeftArc, i * cellSize, j * cellSize, null);
                            break;
                        case UP_TO_RIGHT:
                        case RIGHT_TO_UP:
                            g.drawImage(upToRightArc, i * cellSize, j * cellSize, null);
                            break;
                        case DOWN_TO_LEFT:
                        case LEFT_TO_DOWN:
                            g.drawImage(downToLeftArc, i * cellSize, j * cellSize, null);
                            break;
                        case DOWN_TO_RIGH:
                        case RIGHT_TO_DOWN:
                            g.drawImage(downToRightArc, i * cellSize, j * cellSize, null);
                            break;
                    }
                }
            }
        }

        return bufferedImage;
    }

    private Tile[][] createNewTrack(int size, int seed) {

        Tile[][] tiles = null;
        Random randomGenerator = new Random(seed);

        boolean isUncomplete;
        boolean isTooSmall;
        do {

            isUncomplete = false;
            tiles = new Tile[size][size];
//            long count = Stream.of(tiles).flatMap(Stream::of).filter(tile -> tile != null).count();
//            System.out.println("starting coiunt: " + count);
            // place the first tile
//            int currentY = 1 + randomGenerator.nextInt(SIZE-2), startY = currentY;
//            int currentX = 1 + randomGenerator.nextInt(SIZE-2), startX = currentX;
            int currentY = 0, startY = 0;
            int currentX = size / 2, startX = size / 2;

            tiles[currentX][currentY] = Tile.LEFT_TO_RIGHT;
            Tile startingTile = tiles[startX][startY];

            // place all the others
            do {
                Tile currentTile = tiles[currentX][currentY];

                // gets a random tile from the ones allowed on this place
                java.util.List<Tile> allowedTiles = getAllowedTiles(currentX, currentY, size, tiles);
                if (allowedTiles.size() == 0) {
                    isUncomplete = true;
                    break;
                }

                Tile newTile = null;

                // if this is the last tile to put, checks for the right tile
                for (Tile possibleTile: allowedTiles) {
                    int nextX = currentX + possibleTile.getOutput().getX();
                    int nextY = currentY + possibleTile.getOutput().getY();
                    if (nextX == startX && nextY == startY && possibleTile.getOutput() == startingTile.getInput().getOpposite()) {
                        newTile = possibleTile;
                    }
                }

                if (newTile == null) {
                    newTile = allowedTiles.get(randomGenerator.nextInt(allowedTiles.size()));
                }

                // updates the track
                currentX = currentX + currentTile.getOutput().getX();
                currentY = currentY + currentTile.getOutput().getY();
                tiles[currentX][currentY] = newTile;
            } while (startX != currentX || startY != currentY);

            long count = Stream.of(tiles).flatMap(Stream::of).filter(tile -> tile != null).count();
            isTooSmall = count < (size * size) / 2;

        } while (isUncomplete || isTooSmall);

        return tiles;
    }

    private java.util.List<Tile> getAllowedTiles(int inputX, int inputY, int size, Tile[][] tiles) {

        Tile currentTile = tiles[inputX][inputY];
        int newX = inputX + currentTile.getOutput().getX();
        int newY = inputY + currentTile.getOutput().getY();

        // gets fitting tiles for the current one
        List<Tile> allowedTiles = Arrays.stream(Tile.values())
                .filter(tile -> tile.getInput() == currentTile.getOutput().getOpposite())
                .collect(Collectors.toList());

        // removes tiles that make the track go outside the panel or clashing with already existing tiles
        Iterator<Tile> i = allowedTiles.iterator();
        while (i.hasNext()) {
            Tile tile = i.next();
            if (newX == 0 && tile.getOutput() == Side.LEFT) {
                i.remove();
                continue;
            }
            if (newX == size - 1 && tile.getOutput() == Side.RIGHT) {
                i.remove();
                continue;
            }
            if (newY == 0 && tile.getOutput() == Side.UP) {
                i.remove();
                continue;
            }
            if (newY == size - 1 && tile.getOutput() == Side.DOWN) {
                i.remove();
                continue;
            }

            Tile landingTile = tiles[newX + tile.getOutput().getX()][newY + tile.getOutput().getY()];
            if (landingTile != null && tile.getOutput() != landingTile.getInput().getOpposite()) {
                i.remove();
            }
            if (currentTile.getOutput() == Side.RIGHT && tile.getOutput() == Side.LEFT ||
                    currentTile.getOutput() == Side.LEFT && tile.getOutput() == Side.RIGHT) {
                i.remove();
            }
            if (currentTile.getOutput() == Side.UP && tile.getOutput() == Side.DOWN ||
                    currentTile.getOutput() == Side.DOWN && tile.getOutput() == Side.UP) {
                i.remove();
            }
        }

        return allowedTiles;
    }

    private Image getUpToRightAngle(int arcSize, int cellSize) {
        int cellMid = cellSize / 2;
        return getAngleTile(cellMid, -cellMid, cellSize, arcSize, 180, 90);
    }

    private Image getUpToLeftAngle(int arcSize, int cellSize) {
        int cellMid = cellSize / 2;
        return getAngleTile(-cellMid, -cellMid, cellSize, arcSize, 0, -90);
    }

    private Image getDownToRightAngle(int arcSize, int cellSize) {
        int cellMid = cellSize / 2;
        return getAngleTile(cellMid, cellMid, cellSize, arcSize, 180, -90);
    }

    private Image getDownToLeftAngle(int arcSize, int cellSize) {
        int cellMid = cellSize / 2;
        return getAngleTile(-cellMid, cellMid, cellSize, arcSize, 90, -90);
    }

    private Image getAngleTile(int x, int y, int cellSize, int arcSize, int startingAngle, int angle) {
        int strokeSize = (int) (cellSize / 1.3);
        Image bufferedImage = new BufferedImage(cellSize, cellSize, TYPE_3BYTE_BGR);
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, cellSize, cellSize);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(strokeSize));
        g.drawArc(x, y, arcSize, arcSize, startingAngle, angle);
        return bufferedImage;
    }

    private void print(int size, Tile[][] tiles) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                char c = tiles[j][i] != null ? tiles[j][i].getChar() : '0';
                System.out.print(c);
            }
            System.out.println(" ");
        }
    }
}
