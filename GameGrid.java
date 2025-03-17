import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;

import java.awt.*;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseAdapter;

class GameGrid extends JComponent {
    int numOfCells;
    int boardWidth, boardHeight;
    int top, left;
    int cellWidth, cellHeight;
    int strokeSize = 5;
    Model gameState;

    List<Shot> shots = new ArrayList<>();

    GameGrid(int numOfCells, int boardWidth, int boardHeight, Point topLeft, Model gameState) {
        this.numOfCells = numOfCells;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        left = topLeft.x + strokeSize;
        top = topLeft.y + strokeSize;
        this.cellHeight = boardHeight / numOfCells;
        this.cellWidth = boardWidth / numOfCells;
        this.gameState = gameState;
        removeAll();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Always call the super method first

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(strokeSize));

        try {
            // Load the image
            File waterFile = new File("images/water.jpg");
            BufferedImage waterBackground = ImageIO.read(waterFile);
            // Set position and size

            for (int x = 0; x < numOfCells; x++) {
                for (int y = 0; y < numOfCells; y++) {
                    g2d.drawImage(waterBackground, left + x * cellWidth, top + y * cellHeight, cellWidth, cellHeight,
                            this);
                }
            }

        } catch (Exception e) {
            // System.err.println("Attempted to load from: " + new
            // File("./images/water.jpg").getAbsolutePath());
            e.printStackTrace();
        }

        renderShots();

        // Draws Grid rows
        for (int i = 0; i <= numOfCells; i++) {
            g2d.drawLine(left + i * cellWidth, top, left + i * cellWidth, boardHeight + top);
        }

        // Draws Grid Columns
        for (int i = 0; i <= numOfCells; i++) {
            g2d.drawLine(left, top + i * cellHeight, boardWidth + left, top + i * cellHeight);
        }

    }

    private void renderShots() {
        removeAll();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                int[] cellIndex = { x, y };
                if (gameState.isPlayersTurn()) {
                    Model.CellStatus theirBIndex = gameState.getTheirBoardIndex(cellIndex[0], cellIndex[1]);
                    if (theirBIndex != Model.CellStatus.DONTKNOW) {
                        Shot newShot = new Shot(theirBIndex, cellIndex, GameGrid.this);
                        add(newShot);
                        newShot.setVisible(true);
                    }
                } else {
                    Model.CellStatus yourBIndex = gameState.getHitIndex(cellIndex[0], cellIndex[1]);
                    if (yourBIndex != Model.CellStatus.DONTKNOW) {
                        Shot newShot = new Shot(yourBIndex, cellIndex, GameGrid.this);
                        add(newShot);
                        newShot.setVisible(true);
                    }
                }
            }
        }
        repaint();
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public int[] getCellInside(Point mousePosition) {
        int[] index = new int[2];
        index[0] = (int) (mousePosition.x - left) / cellWidth;
        index[1] = (int) (mousePosition.y - top) / cellHeight;

        // Validates if inside a cell
        if (index[0] > numOfCells || index[1] > numOfCells || (mousePosition.x - left) < 0
                || (mousePosition.y - top) < 0) {
            int[] dummyArr = new int[2];
            dummyArr[0] = -1;
            dummyArr[1] = -1;
            return dummyArr;
        }
        return index;
    }

    public int[] getCellPosition(int[] cellIndex) {
        // Input Validatipn
        if (cellIndex[0] > numOfCells || cellIndex[1] > numOfCells || cellIndex[0] < 0 || cellIndex[1] < 0) {
            int[] dummyArr = new int[2];
            dummyArr[0] = -1;
            dummyArr[1] = -1;
            return dummyArr;
        }

        // Gets Cell Posisiton
        int[] index = new int[2];
        index[0] = cellIndex[0] * cellWidth + left;
        index[1] = cellIndex[1] * cellHeight + top;
        return index;
    }

}