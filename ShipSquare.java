import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import java.awt.*;

class ShipSquare extends JPanel {
    Point imageCoordinates, clickedLocation;

    int xPos, yPos;
    int imageSize;
    int offsetX, offsetY = 0;
    boolean isClicked = false;

    public ShipSquare() {
        xPos = 100;
        yPos = 100;
        imageSize = 80;

        imageCoordinates = new Point(xPos, yPos);

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
    }

    private class DragListener extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent event) {

            if (isClicked) {
                Point currPoint = event.getPoint();

                // imageCoordinates.translate(dx, dy);
                imageCoordinates = currPoint;
                imageCoordinates.translate(offsetX, offsetY);
                xPos = imageCoordinates.x;
                yPos = imageCoordinates.y;

                repaint();
            }
        }
    }

}