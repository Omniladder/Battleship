import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.ObjectOutputStream;

import java.awt.*;
import java.awt.image.*;
import java.awt.image.BufferedImage;

import java.io.*;

import java.awt.*;

class ShipSquare extends JComponent {
    Point clickedLocation;
    ObjectOutputStream out; // this is passed in from view, and pipes output back to client->server
                            // connection. allows position sharing

    int xPos, yPos;
    int imageSize;
    int offsetX, offsetY = 0;
    boolean isClicked = false;
    GameGrid gameGrid;
    Model gameState;
    Image squareImage;
    boolean isRotated;

    public ShipSquare(GameGrid gameGrid, ObjectOutputStream out, Model gameState, boolean isRotated) {
        // Initial Starting Posisiton
        xPos = 100;
        yPos = 100;
        this.gameGrid = gameGrid;
        imageSize = gameGrid.cellHeight;
        this.out = out;
        this.gameState = gameState;
        squareImage = null;
        this.isRotated = isRotated;
    }

    public void assignImage(Image squareImage) {
        this.squareImage = squareImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Always call the super method first

        if (!gameState.isPlayersTurn() || gameState.getCanMoveShips()) {
            // Set the color for the square
            g.setColor(Color.GRAY);

            // Draw a square (x, y, width, height)

            Graphics2D g2d = (Graphics2D) g;
            if (this.squareImage != null) {
                /*
                 * if (isRotated) {
                 * g2d.drawImage(rotateImage((BufferedImage) (this.squareImage)), xPos + 3, yPos
                 * + 3, imageSize - 5,
                 * imageSize - 5, this);
                 * } else {
                 * g2d.drawImage(((BufferedImage) (this.squareImage)), xPos + 3, yPos + 3,
                 * imageSize - 5,
                 * imageSize - 5, this);
                 * }
                 */
                g2d.drawImage(((BufferedImage) (this.squareImage)), xPos + 3, yPos + 3, imageSize - 5,
                        imageSize - 5, this);

            } else {
                g.fillRect(xPos + 3, yPos + 3, imageSize - 5, imageSize - 5); // x=100, y=100, size=100x100
            }
        }
    }

    public void setCellSquare(int x, int y) {
        int[] cellIndex = { x, y };
        if (cellIndex[0] == -1) {
            return;
        }

        int[] cellLocation = gameGrid.getCellPosition(cellIndex);

        xPos = cellLocation[0];
        yPos = cellLocation[1];
    }

    public void setPosition(int x, int y) {
        xPos = x;
        yPos = y;
        repaint();
    }

    public int getXPosition() {
        return xPos;
    }

    public int getYPosition() {
        return yPos;
    }
}