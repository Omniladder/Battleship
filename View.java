import javax.swing.*;
import java.awt.*;

/**
 * View object works as front end to the game takes User Inputs and runs
 */

class SidePanel extends JComponent {
    int width, height = 0;
    Color backgroundColor;

    SidePanel(int width, int height) {
        setSize(width, height);

        this.width = width;
        this.height = height;

        String hexColor = "#003399";
        backgroundColor = Color.decode(hexColor);
        setBackground(backgroundColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Always call the super method first

        // Set the color for the square
        g.setColor(backgroundColor);

        // Draw a square (x, y, width, height)
        g.fillRect(0, 0, width, height);
    }
}

public class View extends JFrame {
    Model gameState;
    ShipSquare testSquare;

    SidePanel gameSide;
    GameGrid gameGrid;

    View(Model gameState, ImageIcon backgroundImage) {

        int boardSize = 950;

        int sidePanelSize = 300;
        setSize(boardSize + sidePanelSize, boardSize);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameGrid = new GameGrid(8, getWidth() - sidePanelSize, getHeight(), new Point(sidePanelSize, 0));
        add(gameGrid);
        setVisible(true);

        // Creates test Square to be used as ship
        testSquare = new ShipSquare(gameGrid);
        add(testSquare);

        setVisible(true);

        // Creates Side Panel to Hold initial Ships
        gameSide = new SidePanel(sidePanelSize, getHeight());
        add(gameSide);

    }
}