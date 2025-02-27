import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import java.awt.*;

class ShipSquare extends JComponent {
    Point clickedLocation;

    int xPos, yPos;
    int imageSize;
    int offsetX, offsetY = 0;
    boolean isClicked = false;
    GameGrid gameGrid;

    public ShipSquare(GameGrid gameGrid) {
        // Initial Starting Posisiton
        xPos = 100;
        yPos = 100;
        this.gameGrid = gameGrid;
        imageSize = gameGrid.cellHeight;

        ClickListener clickListener = new ClickListener();
        this.addMouseListener(clickListener);

        DragListener dragListener = new DragListener();
        this.addMouseMotionListener(dragListener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Always call the super method first

        // Set the color for the square
        g.setColor(Color.GRAY);

        // Draw a square (x, y, width, height)
        g.fillRect(xPos, yPos, imageSize, imageSize); // x=100, y=100, size=100x100
    }

    private class ClickListener extends MouseAdapter {
        public void mousePressed(MouseEvent event) {
            clickedLocation = event.getPoint();
            if (clickedLocation.getX() > xPos && clickedLocation.getX() < xPos + imageSize
                    && clickedLocation.getY() < yPos + imageSize && clickedLocation.getY() > yPos) {
                offsetX = xPos - clickedLocation.x;
                offsetY = yPos - clickedLocation.y;
                isClicked = true;
            } else {
                isClicked = false;
            }
        }

        public void mouseReleased(MouseEvent event) {
            // Add your release action here
            isClicked = false;
            clickedLocation = event.getPoint();

            int[] cellIndex = gameGrid.getCellInside(clickedLocation);
            if (cellIndex[0] == -1) {
                return;
            }

            int[] cellLocation = gameGrid.getCellPosition(cellIndex);

            xPos = cellLocation[0];
            yPos = cellLocation[1];
            repaint();
        }
    }

    private class DragListener extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent event) {

            if (isClicked) {
                Point currPoint = event.getPoint();

                // imageCoordinates.translate(dx, dy);
                currPoint.translate(offsetX, offsetY);
                xPos = currPoint.x;
                yPos = currPoint.y;

                repaint();
            }
        }
    }

}