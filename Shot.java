import javax.swing.JComponent;

import java.awt.*;

class Shot extends JComponent {
    Model.CellStatus hitMiss;
    int xCoord, yCoord;
    GameGrid gameGrid;
    int size;

    Shot(Model.CellStatus hitMiss, int[] coords, GameGrid gameGrid) {
        this.hitMiss = hitMiss;

        this.xCoord = coords[0];
        this.yCoord = coords[1];

        // Get the position in pixels
        int[] position = gameGrid.getCellPosition(coords);

        // THIS IS THE CRITICAL FIX - set the bounds explicitly
        setBounds(position[0], position[1], gameGrid.cellWidth, gameGrid.cellHeight);

        this.gameGrid = gameGrid;

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Always call the super method first

        int[] index = { this.xCoord, this.yCoord };
        index = gameGrid.getCellPosition(index);

        if (hitMiss == Model.CellStatus.HIT) {
            g.setColor(Color.RED);
        } else {
            // System.out.println("Display " + hitMiss);
            g.setColor(Color.LIGHT_GRAY);
        }
        // setComponentZOrder(this, 0);
        // Draw a square (x, y, width, height)
        // g.fillOval(index[0], index[1], gameGrid.getCellWidth(),
        // gameGrid.getCellHeight()); // x=100, y=100, size=100x100
        g.fillOval(2, 2, gameGrid.getCellWidth() - 5, gameGrid.getCellHeight() - 5);

    }

}